package com.rpg.funbox.presentation.game

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.rpg.funbox.R
import com.rpg.funbox.databinding.ActivityGameBinding
import com.rpg.funbox.presentation.game.quiz.QuizViewModel

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private val quizViewModel: QuizViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
    }
}