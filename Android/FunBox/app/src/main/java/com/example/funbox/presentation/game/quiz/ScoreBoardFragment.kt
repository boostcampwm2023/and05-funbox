package com.example.funbox.presentation.game.quiz

import androidx.fragment.app.activityViewModels
import com.example.funbox.R
import com.example.funbox.databinding.FragmentScoreBoardBinding
import com.example.funbox.presentation.BaseDialogFragment


class ScoreBoardFragment : BaseDialogFragment<FragmentScoreBoardBinding>(R.layout.fragment_score_board) {

    private val viewModel: QuizViewModel by activityViewModels()

}