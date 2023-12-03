package com.rpg.funbox.presentation.setting

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
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

    private fun clickWithDraw() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_red_with_text)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        val textView = dialog.findViewById<TextView>(R.id.negativeTextView)
        val button = dialog.findViewById<AppCompatButton>(R.id.dialogNegativeButton)
        button.text = "탈퇴"
        textView.text = "탈퇴하겠습니까?"
        button.setOnClickListener {
            //api
            dialog.dismiss()
        }
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

        is SettingUiEvent.Draw -> {
            clickWithDraw()
        }

        else -> {}
    }
}