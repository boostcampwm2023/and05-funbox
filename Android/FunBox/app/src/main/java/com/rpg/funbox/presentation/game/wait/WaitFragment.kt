package com.rpg.funbox.presentation.game.wait

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.rpg.funbox.R
import com.rpg.funbox.databinding.FragmentWaitBinding
import com.rpg.funbox.presentation.BaseFragment
import com.rpg.funbox.presentation.game.quiz.QuizUiEvent
import com.rpg.funbox.presentation.game.quiz.QuizViewModel

class WaitFragment : BaseFragment<FragmentWaitBinding>(R.layout.fragment_wait) {

    private val viewModel: QuizViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        collectLatestFlow(viewModel.quizUiEvent) { handleUiEvent(it) }

        if (!viewModel.userState.value) {
            findNavController().navigate(R.id.action_WaitFragment_to_QuizFragment)
        }
    }

    private fun handleUiEvent(event: QuizUiEvent) = when (event) {
        is QuizUiEvent.NetworkErrorEvent -> {
            showSnackBar(R.string.network_error_message)
            requireActivity().finish()
        }

        is QuizUiEvent.WaitSuccess -> {
            findNavController().navigate(R.id.action_WaitFragment_to_QuizFragment)
        }

        is QuizUiEvent.QuizFinish -> {
            requireActivity().finish()
        }

        else -> {}
    }
}