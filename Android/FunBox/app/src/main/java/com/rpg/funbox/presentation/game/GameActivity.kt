package com.rpg.funbox.presentation.game

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.rpg.funbox.app.MainApplication
import com.rpg.funbox.data.JwtDecoder
import com.rpg.funbox.databinding.ActivityGameBinding
import com.rpg.funbox.presentation.MapSocket
import com.rpg.funbox.presentation.MapSocket.mSocket
import com.rpg.funbox.presentation.game.quiz.QuizViewModel
import timber.log.Timber

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private val viewModel: QuizViewModel by viewModels()

    private val myUserId =
        JwtDecoder.getUser(MainApplication.mySharedPreferences.getJWT("jwt", "")).id

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        setContentView(binding.root)
        binding.vm = viewModel
        viewModel.connectSocket(myUserId = myUserId)

        initUsersState()

        if(!viewModel.userState.value){
            viewModel.roomId.value?.let { MapSocket.acceptGame(it) }
        }
    }

    private fun initUsersState() {
        viewModel.setRoomId(intent.getStringExtra("RoomId"))
        viewModel.setUserState(intent.getBooleanExtra("StartGame",false))
        intent.getStringExtra("OtherUserId")?.let { viewModel.setUserNames(it.toInt()) }
    }
}