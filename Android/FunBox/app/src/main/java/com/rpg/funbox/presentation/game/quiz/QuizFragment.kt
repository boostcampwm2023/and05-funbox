package com.rpg.funbox.presentation.game.quiz

import android.os.Bundle
import android.view.View
import androidx.annotation.UiThread
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
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
import com.rpg.funbox.presentation.login.AccessPermission

class QuizFragment : BaseFragment<FragmentQuizBinding>(R.layout.fragment_quiz), OnMapReadyCallback {

    private val viewModel: QuizViewModel by activityViewModels()

    private lateinit var quizMap: NaverMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var fusedLocationSource: FusedLocationSource

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        initNaverMap()

        collectLatestFlow(viewModel.quizUiEvent) { handleUiEvent(it) }
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
            AnswerCheckFragment().show(childFragmentManager, "")
        }

        is QuizUiEvent.QuizAnswerCheck -> {
            NetworkAlertFragment().show(childFragmentManager, "")
        }

        is QuizUiEvent.QuizOtherUserDisconnected -> {
            ScoreBoardFragment().show(childFragmentManager, "")
        }

        is QuizUiEvent.QuizScoreBoard -> {
            findNavController().navigate(R.id.action_QuizFragment_to_mapFragment)
        }
    }
}