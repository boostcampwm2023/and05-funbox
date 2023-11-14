package com.example.funbox

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.funbox.databinding.FragmentNicknameBinding

class NicknameFragment : BaseFragment<FragmentNicknameBinding>(R.layout.fragment_nickname) {

    private val viewModel: TitleViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        collectLatestFlow(viewModel.nicknameUiEvent) { handleUiEvent(it) }
    }


    private fun handleUiEvent(event: NicknameUiEvent) = when (event) {
        is NicknameUiEvent.NicknameSuccess -> {
            findNavController().navigate(R.id.action_NicknameFragment_to_ProfileFragment)
        }
        is NicknameUiEvent.NetworkErrorEvent -> {
            showSnackBar(R.string.network_error_message)
        }
    }
}