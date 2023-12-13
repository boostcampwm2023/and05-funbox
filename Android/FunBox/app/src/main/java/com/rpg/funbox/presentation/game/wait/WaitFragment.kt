package com.rpg.funbox.presentation.game.wait

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.rpg.funbox.R
import com.rpg.funbox.databinding.FragmentWaitBinding
import com.rpg.funbox.presentation.BaseFragment
import com.rpg.funbox.presentation.MapSocket
import com.rpg.funbox.presentation.game.quiz.QuizUiEvent
import com.rpg.funbox.presentation.game.quiz.QuizViewModel

class WaitFragment : BaseFragment<FragmentWaitBinding>(R.layout.fragment_wait) {

    private val viewModel: QuizViewModel by activityViewModels()
    private lateinit var backPressedCallback: OnBackPressedCallback
    private var backPressTime: Long = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        collectLatestFlow(viewModel.quizUiEvent) { handleUiEvent(it) }

        if (!viewModel.userState.value) {
            findNavController().navigate(R.id.action_WaitFragment_to_QuizFragment)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        setBackPressedCallback()
    }

    private fun handleUiEvent(event: QuizUiEvent) = when (event) {
        is QuizUiEvent.NetworkErrorEvent -> {
            showSnackBar(R.string.network_error_message)
            requireActivity().finish()
        }

        is QuizUiEvent.WaitSuccess -> {
            findNavController().navigate(R.id.action_WaitFragment_to_QuizFragment)
        }

        is QuizUiEvent.QuizFinish -> {
            requireActivity().finish()
        }

        is QuizUiEvent.OtherPlaying -> {
            showSnackBar(R.string.other_not)
            requireActivity().finish()
        }

        else -> {}
    }

    private fun setBackPressedCallback() {
        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressTime + 3000 > System.currentTimeMillis()) {
                    MapSocket.quitGame()
                    requireActivity().finish()
                } else {
                    Toast.makeText(requireContext(), resources.getString(R.string.finish_quiz_toast_message), Toast.LENGTH_LONG).show()
                    backPressTime = System.currentTimeMillis()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            backPressedCallback
        )
    }
}