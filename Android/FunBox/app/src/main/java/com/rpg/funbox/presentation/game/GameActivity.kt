package com.rpg.funbox.presentation.game

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.rpg.funbox.databinding.ActivityGameBinding
import com.rpg.funbox.presentation.game.quiz.QuizViewModel
import com.rpg.funbox.presentation.game.wait.WaitViewModel
import timber.log.Timber

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private val quizViewModel: QuizViewModel by viewModels()
    private val waitViewModel: WaitViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.waitViewModel = waitViewModel
        binding.quizViewModel = quizViewModel

        quizViewModel.setRoomId(intent.getStringExtra("RoomId"))
        waitViewModel.setRoomId(intent.getStringExtra("RoomId"))

        Timber.d("StartGame: ${intent.getBooleanExtra("StartGame",false)}")
        waitViewModel.setUserState(intent.getBooleanExtra("StartGame",false))
    }
}