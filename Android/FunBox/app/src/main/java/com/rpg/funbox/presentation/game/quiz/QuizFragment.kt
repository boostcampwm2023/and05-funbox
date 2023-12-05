package com.rpg.funbox.presentation.game.quiz

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.UiThread
import androidx.fragment.app.activityViewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.rpg.funbox.R
import com.rpg.funbox.databinding.FragmentQuizBinding
import com.rpg.funbox.presentation.BaseFragment
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource
import com.rpg.funbox.presentation.CustomNaverMap
import com.rpg.funbox.presentation.MapSocket.sendQuizAnswer
import com.rpg.funbox.presentation.MapSocket.verifyAnswer
import com.rpg.funbox.presentation.login.AccessPermission

class QuizFragment : BaseFragment<FragmentQuizBinding>(R.layout.fragment_quiz), OnMapReadyCallback {

    private val viewModel: QuizViewModel by activityViewModels()

    private lateinit var quizMap: NaverMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var fusedLocationSource: FusedLocationSource
    private lateinit var backPressedCallback: OnBackPressedCallback

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        initNaverMap()

        setBackPressedCallback()
        binding.questionQuiz.isSelected = true

        collectLatestFlow(viewModel.quizUiEvent) { handleUiEvent(it) }
    }

    private fun setBackPressedCallback() {
        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 방을 폭파시키는 이벤트를 뿌리는 함수
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            backPressedCallback
        )
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        quizMap = CustomNaverMap.setNaverMap(naverMap, fusedLocationSource)
        quizMap.addOnLocationChangeListener { location ->
            val cameraUpdate = CameraUpdate.scrollTo(LatLng(location.latitude, location.longitude))
            naverMap.moveCamera(cameraUpdate)
            viewModel.setLocation(location.latitude, location.longitude)
        }
    }

    private fun initNaverMap() {
        val cfm = childFragmentManager
        val mapFragment = cfm.findFragmentById(R.id.map_view_quiz) as MapFragment?
            ?: MapFragment.newInstance().also { mapFragment ->
                cfm.beginTransaction().add(R.id.map_view_quiz, mapFragment).commit()
            }
        mapFragment.getMapAsync(this)
        fusedLocationSource =
            FusedLocationSource(this, AccessPermission.LOCATION_PERMISSION_REQUEST_CODE)
    }

    private fun handleUiEvent(event: QuizUiEvent) = when (event) {
        is QuizUiEvent.NetworkErrorEvent -> {
            showSnackBar(R.string.network_error_message)
        }

        is QuizUiEvent.QuizAnswerSubmit -> {
            viewModel.roomId.value?.let { sendQuizAnswer(it, viewModel.latestAnswer.value) }
        }

        is QuizUiEvent.QuizAnswerCheckStart -> {
            AnswerCheckFragment().show(childFragmentManager, "AnswerCheckStart")
        }

        is QuizUiEvent.QuizAnswerCheckRight -> {
            viewModel.roomId.value?.let { verifyAnswer(it, true) }
        }

        is QuizUiEvent.QuizAnswerCheckWrong -> {
            viewModel.roomId.value?.let { verifyAnswer(it, false) }
        }

        is QuizUiEvent.QuizNetworkDisconnected -> {
            NetworkAlertFragment().show(childFragmentManager, "NetworkDisconnected")
        }

        is QuizUiEvent.QuizScoreBoard -> {
            ScoreBoardFragment().show(childFragmentManager, "ScoreBoard")
        }

        else -> {}
    }
}