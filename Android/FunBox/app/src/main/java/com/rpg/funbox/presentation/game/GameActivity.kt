package com.rpg.funbox.presentation.game

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.rpg.funbox.app.MainApplication
import com.rpg.funbox.data.JwtDecoder
import com.rpg.funbox.databinding.ActivityGameBinding
import com.rpg.funbox.presentation.MapSocket
import com.rpg.funbox.presentation.game.quiz.QuizViewModel

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private val viewModel: QuizViewModel by viewModels()

    private val myUserId =
        JwtDecoder.getUser(MainApplication.mySharedPreferences.getJWT("jwt", "")).id
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.vm = viewModel
        binding.lifecycleOwner = this

        viewModel.connectSocket(myUserId = myUserId)

        initUsersState()
    }

    private fun initUsersState() {
        viewModel.setRoomId(intent.getStringExtra("RoomId"))
        viewModel.setUserState(intent.getBooleanExtra("StartGame",false))
        viewModel.setUsersInfo(intent.getIntExtra("OtherUserId", -1))

        if (!viewModel.userState.value) {
            viewModel.roomId.value?.let { MapSocket.acceptGame(it) }
        }
    }
}