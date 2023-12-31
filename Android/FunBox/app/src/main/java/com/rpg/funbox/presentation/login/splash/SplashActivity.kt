package com.rpg.funbox.presentation.login.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.rpg.funbox.app.MainApplication
import com.rpg.funbox.presentation.MainActivity
import com.rpg.funbox.presentation.login.TitleActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // MainApplication.mySharedPreferences.setJWT("jwt", "")

        collectLatestUiEvent()
    }

    private fun collectLatestUiEvent() {
        lifecycleScope.launch {
            viewModel.getUsersLocations(0.0, 0.0)
            viewModel.splashUiEvent.collectLatest { splashUiEvent ->
                when (splashUiEvent) {
                    is SplashUiEvent.NetworkErrorEvent -> {
                        val intent = Intent(this@SplashActivity, TitleActivity::class.java)
                        intent.putExtra("ServerError", true)
                        Timber.d("NetworkError")
                        startActivity(intent)
                    }

                    is SplashUiEvent.Unauthorized -> {
                        val intent = Intent(this@SplashActivity, TitleActivity::class.java)
                        intent.putExtra("ServerError", false)
                        startActivity(intent)
                    }

                    is SplashUiEvent.GetUsersLocationsSuccess -> {
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    }
                }
                this@SplashActivity.finish()
            }
        }
    }
}