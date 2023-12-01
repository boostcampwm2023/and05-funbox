package com.rpg.funbox.presentation.setting

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.rpg.funbox.R
import com.rpg.funbox.databinding.DialogPositiveBinding
import com.rpg.funbox.databinding.DialogProfileChangeBinding
import com.rpg.funbox.presentation.BaseDialogFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SetProfileDialog : BaseDialogFragment<DialogProfileChangeBinding>(R.layout.dialog_profile_change) {
    private val viewModel: SettingViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        lifecycleScope.launch {
            viewModel.settingUiEvent.collectLatest {
                if (it == SettingUiEvent.CloseName) dismiss()
            }
        }
    }
}