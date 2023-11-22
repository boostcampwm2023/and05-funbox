package com.example.funbox.presentation.game.quiz

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.funbox.R
import com.example.funbox.databinding.FragmentAnswerCheckBinding
import com.example.funbox.presentation.BaseDialogFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AnswerCheckFragment : BaseDialogFragment<FragmentAnswerCheckBinding>(R.layout.fragment_answer_check) {

    private val viewModel: QuizViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        lifecycleScope.launch {
            viewModel.quizUiEvent.collectLatest {
                if (it == QuizUiEvent.QuizAnswerCheck) dismiss()
            }
        }
    }
}