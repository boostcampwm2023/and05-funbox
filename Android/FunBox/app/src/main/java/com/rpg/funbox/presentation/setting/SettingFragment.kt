package com.rpg.funbox.presentation.setting

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.naver.maps.map.util.FusedLocationSource
import com.rpg.funbox.R
import com.rpg.funbox.databinding.FragmentSettingBinding
import com.rpg.funbox.presentation.BaseFragment
import com.rpg.funbox.presentation.checkPermission
import com.rpg.funbox.presentation.login.AccessPermission
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate
import com.rpg.funbox.presentation.MapSocket
import com.rpg.funbox.presentation.game.GameActivity
import com.rpg.funbox.presentation.map.GetGameDialog
import com.rpg.funbox.presentation.map.MapViewModel

class SettingFragment : BaseFragment<FragmentSettingBinding>(R.layout.fragment_setting) {

    private val viewModel: SettingViewModel by activityViewModels()
    private val mapViewModel: MapViewModel by activityViewModels()

    private lateinit var locationTimer: Timer
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationSource: FusedLocationSource

    private val requestMultiPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    fusedLocationClient =
                        LocationServices.getFusedLocationProviderClient(requireActivity())
                }

                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    fusedLocationClient =
                        LocationServices.getFusedLocationProviderClient(requireActivity())
                }

                else -> {
                    requireActivity().finish()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationSource = FusedLocationSource(this, AccessPermission.LOCATION_PERMISSION_REQUEST_CODE)
        requestMultiPermissions.launch(AccessPermission.locationPermissionList)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel

        collectLatestFlow(viewModel.settingUiEvent) { handleUiEvent(it) }
        submitUserLocation()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        locationTimer.cancel()
    }

    private fun submitUserLocation() {
        if (requireActivity().checkPermission(AccessPermission.locationPermissionList)) {
            locationTimer = Timer()
            locationTimer.scheduleAtFixedRate(500, 3000) {
                lifecycleScope.launch {
                    val location = fusedLocationClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        null
                    ).await()
                    val makePinDeferred = async {
                        try {
                            viewModel.submitLocation(location.latitude, location.longitude)
                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), resources.getString(R.string.gps_on_toast_message), Toast.LENGTH_LONG).show()
                        }
                    }
                    makePinDeferred.await()
                }
            }
        }
    }

    private fun handleUiEvent(event: SettingUiEvent) = when (event) {
        is SettingUiEvent.GoToMapFragment -> {
            findNavController().navigate(R.id.action_settingFragment_to_mapFragment)
        }

        is SettingUiEvent.SetName -> {
            findNavController().navigate(R.id.action_settingFragment_to_setNameDialog)
        }

        is SettingUiEvent.SetProfile -> {
            findNavController().navigate(R.id.action_settingFragment_to_setProfileDialog)
        }

        is SettingUiEvent.StartWithdrawal -> {
            findNavController().navigate(R.id.action_settingFragment_to_withdrawalDialog)
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