package com.rpg.funbox.presentation.game.quiz

import android.os.Bundle
import android.view.View
import androidx.annotation.UiThread
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.rpg.funbox.R
import com.rpg.funbox.databinding.FragmentQuizBinding
import com.rpg.funbox.presentation.BaseFragment
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource
import com.rpg.funbox.app.MainApplication
import com.rpg.funbox.data.JwtDecoder
import com.rpg.funbox.presentation.CustomNaverMap
import com.rpg.funbox.presentation.MapSocket.mSocket
import com.rpg.funbox.presentation.MapSocket.sendQuizAnswer
import com.rpg.funbox.presentation.MapSocket.verifyAnswer
import com.rpg.funbox.presentation.login.AccessPermission
import com.rpg.funbox.presentation.map.QuizAnswerFromServer
import com.rpg.funbox.presentation.map.QuizFromServer
import com.rpg.funbox.presentation.map.ScoreFromServer
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class QuizFragment : BaseFragment<FragmentQuizBinding>(R.layout.fragment_quiz), OnMapReadyCallback {

    private val viewModel: QuizViewModel by activityViewModels()

    private lateinit var quizMap: NaverMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var fusedLocationSource: FusedLocationSource

    private val myUserId =
        JwtDecoder.getUser(MainApplication.mySharedPreferences.getJWT("jwt", "")).id

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        socketConnect()
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

    private fun socketConnect() {
        mSocket.on("location") {
            Timber.tag("LOCATION").d(it[0].toString())
        }
            .on("quiz") {
                val json = Gson().fromJson(it[0].toString(), QuizFromServer::class.java)
                Timber.tag("퀴즈").d(json.quiz)
                Timber.tag("타겟").d(json.target.toString())

                when (json.target) {
                    myUserId -> {
                        viewModel.setLatestQuiz(json.quiz)
                        viewModel.setUserQuizState(UserQuizState.Quiz)
                    }

                    else -> {
                        viewModel.setUserQuizState(UserQuizState.Answer)
                        lifecycleScope.launch {
                            viewModel.quizUiEvent.collectLatest { uiEvent ->
                                if (uiEvent == QuizUiEvent.QuizAnswerSubmit) sendQuizAnswer(
                                    json.roomId,
                                    viewModel.latestAnswer.value
                                )
                            }
                        }
                    }
                }
            }
            .on("quizAnswer") {
                val json = Gson().fromJson(it[0].toString(), QuizAnswerFromServer::class.java)
                Timber.tag("quizAnswer").d(json.answer)

                viewModel.setLatestAnswer(json.answer)
                viewModel.checkAnswerCorrect()
                lifecycleScope.launch {
                    viewModel.quizUiEvent.collectLatest { uiEvent ->
                        if (uiEvent == QuizUiEvent.QuizAnswerCheckRight) verifyAnswer(
                            json.roomId,
                            true
                        )
                        else if (uiEvent == QuizUiEvent.QuizAnswerCheckWrong) verifyAnswer(
                            json.roomId,
                            false
                        )
                    }
                }
            }
            .on("score") {
                val json = Gson().fromJson(it[0].toString(), ScoreFromServer::class.java)
                Timber.tag("score").d(json.first().toString())
                Timber.tag("score").d(json.last().toString())

                viewModel.setFinalScore(Pair(json.first().toString(), json.last().toString()))
                viewModel.showScoreBoard()
            }
            .on("lostConnection") {
                Timber.tag("lostConnection").d(it.toString())

                viewModel.alertNetworkError()
            }
            .on("error") {
                Timber.tag("ERROR").e(it[0].toString())

                viewModel.setStateNetworkError()
            }
    }

    private fun handleUiEvent(event: QuizUiEvent) = when (event) {
        is QuizUiEvent.NetworkErrorEvent -> {
            showSnackBar(R.string.network_error_message)
        }

        is QuizUiEvent.QuizAnswerCheckStart -> {
            AnswerCheckFragment().show(childFragmentManager, "AnswerCheckStart")
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