package com.rpg.funbox.presentation.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.rpg.funbox.R
import com.rpg.funbox.databinding.DialogGetGameBinding
import com.rpg.funbox.databinding.DialogMessageBinding
import com.rpg.funbox.presentation.BaseDialogFragment

class GetGameDialog : BaseDialogFragment<DialogGetGameBinding>(R.layout.dialog_get_game) {

    private val viewModel: MapViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnOk.setOnClickListener {
            dismiss()
        }
        binding.btnNo.setOnClickListener{
            dismiss()
        }
    }

}