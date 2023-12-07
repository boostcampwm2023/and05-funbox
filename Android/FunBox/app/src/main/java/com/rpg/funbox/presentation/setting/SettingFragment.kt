package com.rpg.funbox.presentation.setting

import android.Manifest
import android.os.Bundle
import android.view.View
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

class SettingFragment : BaseFragment<FragmentSettingBinding>(R.layout.fragment_setting) {

    private val viewModel: SettingViewModel by activityViewModels()

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

    override fun onDetach() {
        super.onDetach()

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
                        viewModel.submitLocation(location.latitude, location.longitude)
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
            // findNavController().navigate(R.id.action_settingFragment_to_SetNameDialog)
            SetNameDialog().show(childFragmentManager, "")
        }

        is SettingUiEvent.SetProfile -> {
            // findNavController().navigate(R.id.action_settingFragment_to_SetProfileDialog)
            SetProfileDialog().show(childFragmentManager, "")
        }

        is SettingUiEvent.StartWithdrawal -> {
            WithdrawalDialog().show(childFragmentManager, "")
            // MainApplication.mySharedPreferences.setJWT("jwt", "")
        }

        else -> {}
    }
}