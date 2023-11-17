package com.example.funbox.presentation.game.gameselect

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.example.funbox.R
import com.example.funbox.databinding.FragmentGameSelectBinding
import com.example.funbox.presentation.BaseFragment

class GameSelectFragment : BaseFragment<FragmentGameSelectBinding>(R.layout.fragment_game_select) {

    private val viewModel: GameSelectViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        collectLatestFlow(viewModel.gameSelectUiEvent) { handleUiEvent(it) }
    }

    private fun handleUiEvent(event: GameSelectUiEvent) = when (event) {
        is GameSelectUiEvent.NetworkErrorEvent -> {
            showSnackBar(R.string.network_error_message)
        }
    }
}