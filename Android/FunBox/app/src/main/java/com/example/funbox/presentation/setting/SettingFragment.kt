package com.example.funbox.presentation.setting

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.viewModels
import com.example.funbox.R
import com.example.funbox.databinding.FragmentProfileBinding
import com.example.funbox.databinding.FragmentSettingBinding
import com.example.funbox.presentation.BaseFragment
import com.example.funbox.presentation.MainActivity
import com.example.funbox.presentation.login.AccessPermission

class SettingFragment : BaseFragment<FragmentSettingBinding>(R.layout.fragment_setting) {

    private val viewModel: SettingViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fm = this

    }

    fun clickNickNameSetting(){
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_message)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        val editText = dialog.findViewById<EditText>(R.id.dialogEditText)
        val button = dialog.findViewById<Button>(R.id.positiveButton)
        button.setOnClickListener {
            //api
            dialog.dismiss()
        }
    }


    fun clickProfileSetting(){
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_profile_change)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        val profileImage = dialog.findViewById<ImageButton>(R.id.dialogProfileImageSettingButton)
        val button = dialog.findViewById<AppCompatButton>(R.id.dialogProfileSettingButton)
        button.setOnClickListener {
            //api
            dialog.dismiss()
        }
    }


    fun clickWithDraw(){
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

}