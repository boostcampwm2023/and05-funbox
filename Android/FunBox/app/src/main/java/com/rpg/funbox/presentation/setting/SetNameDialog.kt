package com.rpg.funbox.presentation.setting

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.rpg.funbox.R
import com.rpg.funbox.databinding.DialogPositiveBinding
import com.rpg.funbox.presentation.BaseDialogFragment
import com.rpg.funbox.presentation.game.quiz.QuizUiEvent
import com.rpg.funbox.presentation.map.MapViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SetNameDialog: BaseDialogFragment<DialogPositiveBinding>(R.layout.dialog_positive) {
    private val viewModel: SettingViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        lifecycleScope.launch {
            viewModel.settingUiEvent.collectLatest {
                if (it == SettingUiEvent.SetName) dismiss()
            }
        }
    }
}