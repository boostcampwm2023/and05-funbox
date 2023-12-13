package com.rpg.funbox.presentation.map

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.rpg.funbox.R
import com.rpg.funbox.databinding.DialogGetGameBinding
import com.rpg.funbox.presentation.BaseDialogFragment
import com.rpg.funbox.presentation.setting.SettingViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GetGameDialog : BaseDialogFragment<DialogGetGameBinding>(R.layout.dialog_get_game) {

    private val viewModel: MapViewModel by activityViewModels()
    private val settingViewModel: SettingViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        isCancelable = false

        lifecycleScope.launch {
            viewModel.mapUiEvent.collectLatest { uiEvent ->
                if (uiEvent == MapUiEvent.CancelGame) dismiss()
            }
        }

        binding.btnOk.setOnClickListener {
            viewModel.toGame()
            settingViewModel.toGame()
            dismiss()
        }

        binding.btnNo.setOnClickListener{
            viewModel.rejectGame()
            settingViewModel.rejectGame()
            dismiss()
        }
    }
}