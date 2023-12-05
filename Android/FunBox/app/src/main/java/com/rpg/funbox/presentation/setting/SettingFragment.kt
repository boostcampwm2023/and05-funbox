package com.rpg.funbox.presentation.setting

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.rpg.funbox.R
import com.rpg.funbox.databinding.FragmentSettingBinding
import com.rpg.funbox.presentation.BaseFragment

class SettingFragment : BaseFragment<FragmentSettingBinding>(R.layout.fragment_setting) {

    private val viewModel: SettingViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        collectLatestFlow(viewModel.settingUiEvent) { handleUiEvent(it) }

    }

    private fun handleUiEvent(event: SettingUiEvent) = when (event) {
        is SettingUiEvent.GoToMapFragment -> {
            findNavController().navigate(R.id.action_settingFragment_to_mapFragment)
        }

        is SettingUiEvent.SetName -> {
            // findNavController().navigate(R.id.action_settingFragment_to_SetNameDialog)
            SetNameDialog().show(childFragmentManager, "")
        }

        is SettingUiEvent.SetProfile -> {
            // findNavController().navigate(R.id.action_settingFragment_to_SetProfileDialog)
            SetProfileDialog().show(childFragmentManager, "")
        }

        is SettingUiEvent.StartWithdrawal -> {
            WithdrawalDialog().show(childFragmentManager, "")
        }

        else -> {}
    }
}