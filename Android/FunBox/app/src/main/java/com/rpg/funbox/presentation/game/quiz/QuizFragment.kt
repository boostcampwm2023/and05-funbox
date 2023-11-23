package com.rpg.funbox.presentation.game.quiz

import android.os.Bundle
import android.view.View
import androidx.annotation.UiThread
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.rpg.funbox.R
import com.rpg.funbox.databinding.FragmentQuizBinding
import com.rpg.funbox.presentation.BaseFragment
import com.naver.maps.map.MapFragment
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback

class QuizFragment : BaseFragment<FragmentQuizBinding>(R.layout.fragment_quiz), OnMapReadyCallback {

    private val viewModel: QuizViewModel by activityViewModels()
    private lateinit var mapView: MapView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        mapView = binding.mapViewGame
        mapView.onCreate(savedInstanceState)
        setNaverMap()

        collectLatestFlow(viewModel.quizUiEvent) { handleUiEvent(it) }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    @UiThread
    override fun onMapReady(p0: NaverMap) {

    }

    private fun setNaverMap() {
        val cfm = childFragmentManager
        val mapFragment = cfm.findFragmentById(R.id.map_view_game) as MapFragment?
            ?: MapFragment.newInstance().also { mapFragment ->
                cfm.beginTransaction().add(R.id.map_view_game, mapFragment).commit()
            }
        mapFragment.getMapAsync(this)
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