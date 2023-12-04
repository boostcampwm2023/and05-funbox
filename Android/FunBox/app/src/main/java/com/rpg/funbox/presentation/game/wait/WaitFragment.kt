package com.rpg.funbox.presentation.game.wait

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.rpg.funbox.R
import com.rpg.funbox.databinding.FragmentWaitBinding
import com.rpg.funbox.presentation.BaseFragment
import com.rpg.funbox.presentation.MainActivity
import com.rpg.funbox.presentation.MapSocket.mSocket
import com.rpg.funbox.presentation.game.quiz.QuizUiEvent
import com.rpg.funbox.presentation.game.quiz.QuizViewModel
import com.rpg.funbox.presentation.map.GameApplyAnswerFromServerData
import timber.log.Timber

class WaitFragment : BaseFragment<FragmentWaitBinding>(R.layout.fragment_wait) {

    private val viewModel: QuizViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        collectLatestFlow(viewModel.quizUiEvent) { handleUiEvent(it) }

        if (viewModel.userState.value) {
            connectSocket()
        } else {
            Timber.d("User State: ${viewModel.userState.value}")
            findNavController().navigate(R.id.action_WaitFragment_to_QuizFragment)
        }
    }

    private fun connectSocket(){
        mSocket.on("gameApplyAnswer"){
            val json = Gson().fromJson(it[0].toString(), GameApplyAnswerFromServerData::class.java)
            Timber.tag("gameApplyAnswer").d(json.answer)
            when (json.answer){
                "OFFLINE"->{
                    val intent = Intent(context, MainActivity::class.java)
                    startActivity(intent)
                }
                "ACCEPT"->{
                    viewModel.toGame()
                }
                "REJECT"->{
                    val intent = Intent(context, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun handleUiEvent(event: QuizUiEvent) = when (event) {
        is QuizUiEvent.NetworkErrorEvent -> {
            showSnackBar(R.string.network_error_message)
        }

        is QuizUiEvent.WaitSuccess -> {
            findNavController().navigate(R.id.action_WaitFragment_to_QuizFragment)
        }

        else -> {}
    }
}