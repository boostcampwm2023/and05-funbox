package com.rpg.funbox.presentation.game.quiz

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.UiThread
import androidx.fragment.app.activityViewModels
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
import com.rpg.funbox.presentation.CustomNaverMap
import com.rpg.funbox.presentation.MapSocket.mSocket
import com.rpg.funbox.presentation.login.AccessPermission
import com.rpg.funbox.presentation.map.GameApplyAnswerFromServerData
import com.rpg.funbox.presentation.map.QuizAnswerFromServer
import com.rpg.funbox.presentation.map.QuizAnswerToServer
import com.rpg.funbox.presentation.map.QuizFromServer
import com.rpg.funbox.presentation.map.ScoreFromServer
import com.rpg.funbox.presentation.map.VerifyAnswerToServer
import org.json.JSONObject

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
            requireActivity().finish()
        }
    }

    private fun socket(){
        mSocket.on("location"){
            Log.d("LOCATION",it[0].toString())
        }
            .on("gameApplyAnswer"){
                val json = Gson().fromJson(it[0].toString(), GameApplyAnswerFromServerData::class.java)
                Log.d("gameApplyAnswer",json.answer)
                when (json.answer){
                    "OFFLINE"->{
                        // 네트워크 연결끊김
                    }
                    "ACCEPT"->{
                        // 게임화면
                    }
                    "REJECT"->{
                        // 메인 화면
                    }
                }
            }
            .on("quiz"){
                val json = Gson().fromJson(it[0].toString(), QuizFromServer::class.java)
                Log.d("퀴즈",json.quiz)
                Log.d("타겟",json.target.toString())

                if (json.target != 35){//내id)
                    // UI에서 퀴즈 띄워주기
                }
                else{
                    // 답입력창 띄워주기
                    val answer = "답입니다."
                    sendQuizAnswer(json.roomId,answer)
                }
            }
            .on("quizAnswer"){
                val json = Gson().fromJson(it[0].toString(), QuizAnswerFromServer::class.java)
                Log.d("quizAnswer",json.answer)

                // UI에서 맞는지 체크
                val isCorrect = true
                verifyAnswer(json.roomId,isCorrect)

            }
            .on("score"){
                val json = Gson().fromJson(it[0].toString(), ScoreFromServer::class.java)
                Log.d("score",json.first().toString())
                Log.d("score",json.last().toString())
            }
            .on("lostConnection"){
                Log.d("lostConnection",it.toString())
            }
            .on("error"){
                Log.e("ERROR",it[0].toString())
            }
    }

    private fun verifyAnswer(roomId: String, isCorrect: Boolean) {
        val json = Gson().toJson(VerifyAnswerToServer(isCorrect,roomId))
        mSocket.emit("verifyAnswer", JSONObject(json))
    }

    private fun sendQuizAnswer(roomId: String, answer: String) {
        val json = Gson().toJson(QuizAnswerToServer(answer,roomId))
        mSocket.emit("quizAnswer", JSONObject(json))
    }

}