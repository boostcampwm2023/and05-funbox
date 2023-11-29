package com.rpg.funbox.presentation.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.rpg.funbox.R
import com.rpg.funbox.databinding.DialogMessageBinding
import com.rpg.funbox.presentation.BaseDialogFragment

class MessageDialog: BaseDialogFragment<DialogMessageBinding>(R.layout.dialog_message) {

    private val viewModel: MapViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.positiveButton.setOnClickListener{
            dismiss()
        }
    }

}