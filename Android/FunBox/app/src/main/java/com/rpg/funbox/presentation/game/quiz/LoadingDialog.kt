package com.rpg.funbox.presentation.game.quiz

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rpg.funbox.R
import com.rpg.funbox.databinding.DialogLoadingBinding
import com.rpg.funbox.presentation.BaseDialogFragment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class LoadingDialog : BaseDialogFragment<DialogLoadingBinding>(R.layout.dialog_loading) {

    private val viewModel: QuizViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        isCancelable = false

        lifecycleScope.launch {
            viewModel.quizUiState.collect {
                Timber.d(viewModel.quizUiState.value.isUserQuizState.toString())
                if (it.isUserQuizState) {
                    dismiss()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.quizUiEvent.collectLatest { uiEvent ->
                if (uiEvent == QuizUiEvent.QuizScoreBoard) {
                    findNavController().navigate(R.id.action_loadingDialog_to_scoreBoardFragment)
                }
            }
        }
    }

}