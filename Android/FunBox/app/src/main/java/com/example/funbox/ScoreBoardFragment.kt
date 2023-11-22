package com.example.funbox

import androidx.fragment.app.activityViewModels
import com.example.funbox.databinding.FragmentScoreBoardBinding
import com.example.funbox.presentation.BaseDialogFragment
import com.example.funbox.presentation.game.quiz.QuizViewModel


class ScoreBoardFragment : BaseDialogFragment<FragmentScoreBoardBinding>(R.layout.fragment_score_board) {

    private val viewModel: QuizViewModel by activityViewModels()

}