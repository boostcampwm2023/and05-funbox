package com.rpg.funbox.presentation.login.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.rpg.funbox.presentation.MainActivity
import com.rpg.funbox.presentation.login.TitleActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            viewModel.getUsersLocations()
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