package com.rpg.funbox.presentation.map

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.rpg.funbox.R
import com.rpg.funbox.databinding.DialogGetGameBinding
import com.rpg.funbox.presentation.BaseDialogFragment

class GetGameDialog : BaseDialogFragment<DialogGetGameBinding>(R.layout.dialog_get_game) {

    private val viewModel: MapViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        isCancelable=false
        binding.btnOk.setOnClickListener {
            viewModel.toGame()
            dismiss()
        }
        binding.btnNo.setOnClickListener{
            viewModel.rejectGame()
            dismiss()
        }
    }

}