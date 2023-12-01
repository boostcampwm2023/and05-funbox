package com.rpg.funbox.presentation.login.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.rpg.funbox.presentation.MainActivity
import com.rpg.funbox.presentation.checkPermission
import com.rpg.funbox.presentation.login.AccessPermission
import com.rpg.funbox.presentation.login.TitleActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val viewModel: SplashViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val requestMultiPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestLocationPermissions()
        collectLatestUiEvent()
    }

    private fun requestLocationPermissions() {
        requestMultiPermissions.launch(AccessPermission.locationPermissionList)
        if (checkPermission(AccessPermission.locationPermissionList)) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@SplashActivity)
        } else {
            finish()
        }
        lifecycleScope.launch {
            val tmp = fusedLocationClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, null).await()
            if (tmp != null) {
                viewModel.getUsersLocations(tmp.latitude, tmp.longitude)
            }
        }
    }

    private fun collectLatestUiEvent() {
        lifecycleScope.launch {
            viewModel.splashUiEvent.collectLatest { splashUiEvent ->
                when (splashUiEvent) {
                    is SplashUiEvent.Unauthorized -> {
                        startActivity(Intent(this@SplashActivity, TitleActivity::class.java))
                        this@SplashActivity.finish()
                    }

                    is SplashUiEvent.GetUsersLocationsSuccess -> {
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        this@SplashActivity.finish()
                    }
                }
            }
        }
    }
}