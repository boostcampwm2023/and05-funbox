package com.rpg.funbox.presentation.game.wait

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.rpg.funbox.R
import com.rpg.funbox.databinding.FragmentWaitBinding
import com.rpg.funbox.presentation.BaseFragment
import com.rpg.funbox.presentation.MainActivity
import com.rpg.funbox.presentation.MapSocket.mSocket
import com.rpg.funbox.presentation.game.GameActivity
import com.rpg.funbox.presentation.map.GameApplyAnswerFromServerData

class WaitFragment : BaseFragment<FragmentWaitBinding>(R.layout.fragment_wait) {

    private val viewModel: WaitViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        collectLatestFlow(viewModel.waitUiEvent) { handleUiEvent(it) }

        socket()
    }

    private fun handleUiEvent(event: WaitUiEvent) = when (event) {
        is WaitUiEvent.NetworkErrorEvent -> {
            showSnackBar(R.string.network_error_message)
        }

        is WaitUiEvent.WaitSuccess -> {
            findNavController().navigate(R.id.action_WaitFragment_to_QuizFragment)
        }
    }

    private fun socket(){
        mSocket.on("gameApplyAnswer"){
            val json = Gson().fromJson(it[0].toString(), GameApplyAnswerFromServerData::class.java)
            Log.d("gameApplyAnswer",json.answer)
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
}