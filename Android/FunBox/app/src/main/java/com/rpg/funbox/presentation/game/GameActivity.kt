package com.rpg.funbox.presentation.game

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.rpg.funbox.databinding.ActivityGameBinding
import com.rpg.funbox.presentation.game.quiz.QuizViewModel

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private val viewModel: QuizViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.vm = viewModel

        initUsersState()
    }

    private fun initUsersState() {
        viewModel.setRoomId(intent.getStringExtra("RoomId"))
        viewModel.setUserState(intent.getBooleanExtra("StartGame",false))
        viewModel.setUserNames(intent.getIntExtra("OtherUserId", -1))
    }
}