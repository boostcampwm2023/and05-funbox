package com.rpg.funbox.presentation.game

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.rpg.funbox.R
import com.rpg.funbox.databinding.ActivityGameBinding
import com.rpg.funbox.presentation.game.quiz.QuizViewModel
import com.rpg.funbox.presentation.game.wait.WaitViewModel

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private val quizViewModel: QuizViewModel by viewModels()
    private val waitViewModel: WaitViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.getBooleanExtra("StartGame",false)?.let { waitViewModel.setUserState(it) }
    }
}