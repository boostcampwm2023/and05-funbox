package com.rpg.funbox.presentation.setting

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.rpg.funbox.R
import com.rpg.funbox.databinding.FragmentSettingBinding
import com.rpg.funbox.presentation.BaseFragment
import com.rpg.funbox.presentation.MapSocket
import com.rpg.funbox.presentation.MapSocket.mSocket
import com.rpg.funbox.presentation.game.GameActivity
import com.rpg.funbox.presentation.map.ApplyGameFromServerData
import com.rpg.funbox.presentation.map.GetGameDialog
import com.rpg.funbox.presentation.map.MapUiEvent
import com.rpg.funbox.presentation.map.MapViewModel
import timber.log.Timber

class SettingFragment : BaseFragment<FragmentSettingBinding>(R.layout.fragment_setting) {

    private val viewModel: SettingViewModel by activityViewModels()
    private val mapViewModel: MapViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        collectLatestFlow(viewModel.settingUiEvent) { handleUiEvent(it) }

    }

    private fun handleUiEvent(event: SettingUiEvent) = when (event) {
        is SettingUiEvent.GoToMapFragment -> {
            findNavController().navigate(R.id.action_settingFragment_to_mapFragment)
        }

        is SettingUiEvent.SetName -> {
            findNavController().navigate(R.id.action_settingFragment_to_setNameDialog)
            //SetNameDialog().show(childFragmentManager, "")
        }

        is SettingUiEvent.SetProfile -> {
            findNavController().navigate(R.id.action_settingFragment_to_setProfileDialog)
            //SetProfileDialog().show(childFragmentManager, "")
        }

        is SettingUiEvent.StartWithdrawal -> {
            findNavController().navigate(R.id.action_settingFragment_to_withdrawalDialog)
            //WithdrawalDialog().show(childFragmentManager, "")
        }

        is SettingUiEvent.ToGame -> {
            val intent = Intent(context, GameActivity::class.java)
            intent.putExtra("StartGame", false)
            intent.putExtra("RoomId", mapViewModel.applyGameFromServerData.value?.roomId)
            intent.putExtra(
                "OtherUserId",
                mapViewModel.applyGameFromServerData.value?.userId?.toInt()
            )
            startActivity(intent)
        }

        is SettingUiEvent.RejectGame -> {
            mapViewModel.applyGameFromServerData.value?.roomId?.let { MapSocket.rejectGame(it) }
        }

        is SettingUiEvent.GetGame -> {
            //findNavController().navigate(R.id.action_settingFragment_to_getGameDialog)
            GetGameDialog().show(parentFragmentManager, "getGame")
        }

        else -> {}
    }
}