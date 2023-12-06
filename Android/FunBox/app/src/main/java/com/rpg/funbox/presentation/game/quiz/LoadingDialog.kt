package com.rpg.funbox.presentation.game.quiz

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.rpg.funbox.R
import com.rpg.funbox.databinding.DialogLoadingBinding
import com.rpg.funbox.presentation.BaseDialogFragment

class LoadingDialog : BaseDialogFragment<DialogLoadingBinding>(R.layout.dialog_loading) {

    private val viewModel: QuizViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm=viewModel
        isCancelable=false
        if(viewModel.quizUiState.value.isUserQuizState){
            dismiss()
        }
    }

}