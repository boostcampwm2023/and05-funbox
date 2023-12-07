package com.rpg.funbox.presentation.game.quiz

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.rpg.funbox.R
import com.rpg.funbox.databinding.FragmentNetworkAlertBinding
import com.rpg.funbox.presentation.BaseDialogFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NetworkAlertFragment : BaseDialogFragment<FragmentNetworkAlertBinding>(R.layout.fragment_network_alert) {

    private val viewModel: QuizViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        isCancelable=false
        lifecycleScope.launch {
            viewModel.quizUiEvent.collectLatest {
                if (it == QuizUiEvent.QuizFinish) {
                    requireActivity().finish()
                }
            }
        }
    }
}