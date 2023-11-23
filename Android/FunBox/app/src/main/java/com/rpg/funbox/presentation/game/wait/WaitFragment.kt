package com.rpg.funbox.presentation.game.wait

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rpg.funbox.R
import com.rpg.funbox.databinding.FragmentWaitBinding
import com.rpg.funbox.presentation.BaseFragment

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

        is WaitUiEvent.WaitSuccess -> {
            findNavController().navigate(R.id.action_WaitFragment_to_QuizFragment)
        }
    }
}