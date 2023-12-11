package com.rpg.funbox.presentation.map

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.rpg.funbox.R
import com.rpg.funbox.databinding.DialogMessageBinding
import com.rpg.funbox.presentation.BaseDialogFragment
import com.rpg.funbox.presentation.setting.SettingViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MessageDialog: BaseDialogFragment<DialogMessageBinding>(R.layout.dialog_message) {

    private val viewModel: MapViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        lifecycleScope.launch {
            viewModel.mapUiEvent.collectLatest { uiEvent ->
                if (uiEvent == MapUiEvent.MessageSubmit) dismiss()

                if (uiEvent == MapUiEvent.CancelGame) dismiss()
            }
        }
    }
}