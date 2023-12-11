package com.rpg.funbox.presentation

import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.ActivityNavigator
import coil.load
import com.google.gson.Gson
import com.rpg.funbox.R
import com.rpg.funbox.databinding.ActivityMainBinding
import com.rpg.funbox.presentation.login.TitleActivity
import com.rpg.funbox.presentation.map.ApplyGameFromServerData
import com.rpg.funbox.presentation.map.MapViewModel
import com.rpg.funbox.presentation.setting.SettingUiEvent
import com.rpg.funbox.presentation.setting.SettingViewModel
import io.socket.client.Socket
import io.socket.engineio.client.EngineIOException
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val mapViewModel: MapViewModel by viewModels()
    private val settingViewModel: SettingViewModel by viewModels()
    private lateinit var applyGameServerData: ApplyGameFromServerData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        socketConnect()

        binding.ibSide.setOnClickListener { binding.drawerLayout.openDrawer(GravityCompat.START) }
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_map -> {
                    settingViewModel.goToMap()
                }

                R.id.menu_setting -> {
                    mapViewModel.toSetting()
                }
            }
            binding.drawerLayout.closeDrawers()
            return@setNavigationItemSelectedListener false
        }
        settingViewModel.initUserInfo()
        lifecycleScope.launch {
            settingViewModel.user.collect { user ->
                user?.let {
                    binding.navView.getHeaderView(0)
                        .findViewById<TextView>(R.id.tv_menu_header_name).text = it.userName
                }
            }
        }
        lifecycleScope.launch {
            settingViewModel.profileUri.collect { uri ->
                if (uri == null) {
                    binding.navView.getHeaderView(0).findViewById<ImageView>(R.id.iv_menu_header)
                        .load(R.drawable.profile_none)
                } else {
                    binding.navView.getHeaderView(0).findViewById<ImageView>(R.id.iv_menu_header)
                        .load(uri)
                }
            }
        }
        lifecycleScope.launch {
            settingViewModel.settingUiEvent.collectLatest { uiEvent ->
                if (uiEvent == SettingUiEvent.Withdraw) {
                    val intent = Intent(this@MainActivity, TitleActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action != MotionEvent.ACTION_DOWN) {
            return super.dispatchTouchEvent(event)
        }


        if (this.currentFocus is EditText) {
            val outRect = Rect()
            this.currentFocus?.let {
                it.getGlobalVisibleRect(outRect)
                hideKeyboard(outRect, event, it)
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun hideKeyboard(
        outRect: Rect,
        event: MotionEvent,
        it: View
    ) {
        if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
            it.clearFocus()

            val inputMethodManager: InputMethodManager =
                this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                it.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    private fun socketConnect() {
        MapSocket.mSocket.connect()
        MapSocket.mSocket.on(Socket.EVENT_CONNECT) {
            Timber.tag("Connect").d("SOCKET CONNECT")
        }.on(Socket.EVENT_DISCONNECT) { _ ->
            Timber.tag("Connect").d("SOCKET DISCONNECT")
        }.on(Socket.EVENT_CONNECT_ERROR) { args ->
            if (args[0] is EngineIOException) Timber.tag("Disconnect").d("SOCKET ERROR")
        }.on("gameApply") {
            applyGameServerData =
                Gson().fromJson(it[0].toString(), ApplyGameFromServerData::class.java)
            Timber.d("Other Id: ${applyGameServerData.userId}")
            mapViewModel.setOtherUser(applyGameServerData.userId.toInt())
            mapViewModel.setApplyGameData(applyGameServerData)
            mapViewModel.getGame()
            settingViewModel.getGame()
        }.on("quitGame") {
            mapViewModel.cancelGame()
            Toast.makeText(
                this,
                "상대방이 게임을 취소하였습니다.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}