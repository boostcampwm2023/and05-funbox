package com.rpg.funbox.presentation.login.nickname

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.rpg.funbox.R
import com.rpg.funbox.databinding.FragmentNicknameBinding
import com.rpg.funbox.presentation.BaseFragment
import com.rpg.funbox.presentation.login.title.TitleViewModel

class NicknameFragment : BaseFragment<FragmentNicknameBinding>(R.layout.fragment_nickname) {

    private val viewModel: TitleViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        collectLatestFlow(viewModel.nicknameUiEvent) { handleUiEvent(it) }
    }

    private fun handleUiEvent(event: NicknameUiEvent) = when (event) {
        is NicknameUiEvent.NicknameSubmit -> {
            findNavController().navigate(R.id.action_NicknameFragment_to_ProfileFragment)
        }

        is NicknameUiEvent.NetworkErrorEvent -> {
            showSnackBar(R.string.network_error_message)
        }
    }
}