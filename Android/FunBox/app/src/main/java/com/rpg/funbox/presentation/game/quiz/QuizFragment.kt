package com.rpg.funbox.presentation.game.quiz

import android.Manifest
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.UiThread
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.rpg.funbox.R
import com.rpg.funbox.databinding.FragmentQuizBinding
import com.rpg.funbox.presentation.BaseFragment
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.rpg.funbox.presentation.CustomNaverMap
import com.rpg.funbox.presentation.MapSocket
import com.rpg.funbox.presentation.MapSocket.send
import com.rpg.funbox.presentation.MapSocket.sendQuizAnswer
import com.rpg.funbox.presentation.MapSocket.verifyAnswer
import com.rpg.funbox.presentation.checkPermission
import com.rpg.funbox.presentation.login.AccessPermission
import com.rpg.funbox.presentation.login.AccessPermission.LOCATION_PERMISSION_REQUEST_CODE
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate

class QuizFragment : BaseFragment<FragmentQuizBinding>(R.layout.fragment_quiz), OnMapReadyCallback {

    private val viewModel: QuizViewModel by activityViewModels()

    private lateinit var locationTimer: Timer
    private lateinit var quizMap: NaverMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var fusedLocationSource: FusedLocationSource
    private lateinit var backPressedCallback: OnBackPressedCallback

    private var backPressTime: Long = 0

    private val requestMultiPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    fusedLocationProviderClient =
                        LocationServices.getFusedLocationProviderClient(requireActivity())
                }

                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    fusedLocationProviderClient =
                        LocationServices.getFusedLocationProviderClient(requireActivity())
                }

                else -> {
                    requireActivity().finish()
                }
            }
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        setBackPressedCallback()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        requestMultiPermissions.launch(AccessPermission.locationPermissionList)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        binding.questionQuiz.isSelected = true

        collectLatestFlow(viewModel.quizUiEvent) { handleUiEvent(it) }

        initNaverMap()
        submitUserLocation()
        
        lifecycleScope.launch {
            viewModel.chatMessages.collect{
                viewModel.chatAdapter.submitList(it)
                viewModel.chatAdapter.notifyDataSetChanged()
                Timber.d(viewModel.chatMessages.value.size.toString())
                binding.rvChat.smoothScrollToPosition(viewModel.chatMessages.value.size)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        locationTimer.cancel()
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        quizMap = CustomNaverMap.setNaverMap(naverMap, fusedLocationSource)
        quizMap.addOnLocationChangeListener { location ->
            val cameraUpdate = CameraUpdate.scrollTo(LatLng(location.latitude, location.longitude))
            naverMap.moveCamera(cameraUpdate)
            viewModel.setLocation(location.latitude, location.longitude)
        }
        quizMap = CustomNaverMap.setNaverMapLocationOverlay(quizMap)

        lifecycleScope.launch {
            viewModel.prevOtherUser.value?.let { user ->
                user.mapPin?.map = null
            }
            viewModel.nowOtherUser.value?.let { user ->
                user.mapPin = Marker().apply {
                    this.position = user.loc
                    iconTintColor = Color.YELLOW
                    captionText = user.name.toString()
                }
                user.mapPin?.map = quizMap
            }
        }
    }

    private fun initNaverMap() {
        binding.mapViewQuiz.getFragment<MapFragment>().getMapAsync(this)
        fusedLocationSource =
            FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    }

    private fun submitUserLocation() {
        if (requireActivity().checkPermission(AccessPermission.locationPermissionList)) {
            locationTimer = Timer()
            locationTimer.scheduleAtFixedRate(500, 3000) {
                lifecycleScope.launch {
                    val drawPinDeferred = async {
                        binding.mapViewQuiz.getFragment<MapFragment>()
                            .getMapAsync(this@QuizFragment)
                    }
                    drawPinDeferred.await()
                    val location = fusedLocationProviderClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        null
                    ).await()
                    val makePinDeferred = async {
                        try {
                            viewModel.setUsersLocations(location.latitude, location.longitude)
                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), resources.getString(R.string.gps_on_toast_message), Toast.LENGTH_LONG).show()
                        }
                    }
                    makePinDeferred.await()
                }
            }
        }
    }

    private fun setBackPressedCallback() {
        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressTime + 3000 > System.currentTimeMillis()) {
                    viewModel.roomId.value?.let { roomId ->
                        MapSocket.quitGame(roomId)
                    }
                    requireActivity().finish()
                } else {
                    Toast.makeText(requireContext(), resources.getString(R.string.finish_quiz_toast_message), Toast.LENGTH_LONG).show()
                    backPressTime = System.currentTimeMillis()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            backPressedCallback
        )
    }

    private fun handleUiEvent(event: QuizUiEvent) = when (event) {
        is QuizUiEvent.NetworkErrorEvent -> {
            showSnackBar(R.string.network_error_message)
        }

        is QuizUiEvent.QuizAnswerSubmit -> {
            findNavController().navigate(R.id.action_QuizFragment_to_loadingDialog)
            viewModel.roomId.value?.let { sendQuizAnswer(it, viewModel.latestAnswer.value) }
        }

        is QuizUiEvent.QuizAnswerCheckStart -> {
            findNavController().navigate(R.id.action_QuizFragment_to_answerCheckFragment)
        }

        is QuizUiEvent.QuizAnswerCheckRight -> {
            viewModel.roomId.value?.let { verifyAnswer(it, true) }
        }

        is QuizUiEvent.QuizAnswerCheckWrong -> {
            viewModel.roomId.value?.let { verifyAnswer(it, false) }
        }

        is QuizUiEvent.QuizNetworkDisconnected -> {
            findNavController().navigate(R.id.action_QuizFragment_to_networkAlertFragment)
        }

        is QuizUiEvent.QuizScoreBoard -> {
            viewModel.setUserQuizStateTrue()
            findNavController().navigate(R.id.action_QuizFragment_to_scoreBoardFragment)
        }

        is QuizUiEvent.SendMessage -> {
            send(viewModel.otherUserId.value, viewModel.sendingMessage.value)
            //Timber.d("${viewModel.chatMessages.value}")
        }

        else -> {}
    }
}