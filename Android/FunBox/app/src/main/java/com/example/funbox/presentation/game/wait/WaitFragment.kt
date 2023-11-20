package com.example.funbox.presentation.game.wait

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.example.funbox.R
import com.example.funbox.databinding.FragmentWaitBinding
import com.example.funbox.presentation.BaseFragment

class WaitFragment : BaseFragment<FragmentWaitBinding>(R.layout.fragment_wait) {

    private val viewModel: WaitViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        collectLatestFlow(viewModel.waitUiEvent) { handleUiEvent(it) }
    }

    private fun handleUiEvent(event: WaitUiEvent) = when (event) {
        is WaitUiEvent.NetworkErrorEvent -> {
            showSnackBar(R.string.network_error_message)
        }
    }
}