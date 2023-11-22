package com.example.funbox

import androidx.fragment.app.activityViewModels
import com.example.funbox.databinding.FragmentNetworkAlertBinding
import com.example.funbox.presentation.BaseDialogFragment
import com.example.funbox.presentation.game.quiz.QuizViewModel

class NetworkAlertFragment : BaseDialogFragment<FragmentNetworkAlertBinding>(R.layout.fragment_network_alert) {

    private val viewModel: QuizViewModel by activityViewModels()

}