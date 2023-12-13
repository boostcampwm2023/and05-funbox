package com.rpg.funbox.presentation.setting

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.rpg.funbox.R
import com.rpg.funbox.databinding.DialogRedWithTextBinding
import com.rpg.funbox.presentation.BaseDialogFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class WithdrawalDialog : BaseDialogFragment<DialogRedWithTextBinding>(R.layout.dialog_red_with_text) {

    private val viewModel: SettingViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        lifecycleScope.launch {
            viewModel.settingUiEvent.collectLatest { uiEvent ->
                if (uiEvent == SettingUiEvent.Withdraw) {
                    dismiss()
                }
            }
        }
    }
}