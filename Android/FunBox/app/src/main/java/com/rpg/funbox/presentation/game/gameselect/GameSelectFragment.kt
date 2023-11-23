package com.rpg.funbox.presentation.game.gameselect

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rpg.funbox.R
import com.rpg.funbox.databinding.FragmentGameSelectBinding
import com.rpg.funbox.presentation.BaseFragment
import kotlinx.coroutines.launch
import timber.log.Timber

class GameSelectFragment : BaseFragment<FragmentGameSelectBinding>(R.layout.fragment_game_select) {

    private val viewModel: GameSelectViewModel by viewModels()
    private val viewModel2: QuestionGameViewModel by viewModels()
    private val entry = listOf(1, 5, 10, 20)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModel
        binding.gameListAdapter = GameListAdapter(viewModel)

        lifecycleScope.launch {
            viewModel2.quizQuestionCount.collect {
                Timber.d("Count: $it")
            }
        }

        collectLatestFlow(viewModel.gameSelectUiEvent) { handleUiEvent(it) }
    }

    private fun handleUiEvent(event: GameSelectUiEvent) = when (event) {
        is GameSelectUiEvent.NetworkErrorEvent -> {
            showSnackBar(R.string.network_error_message)
        }

        is GameSelectUiEvent.GameSelectSuccess -> {
            Timber.d("Question Count: ${viewModel2.quizQuestionCount.value}")
            findNavController().navigate(R.id.action_GameSelectFragment_to_WaitFragment)
        }

        is GameSelectUiEvent.GameListSubmit -> {
            viewModel2.setSpinnerEntry(entry)
        }
    }
}