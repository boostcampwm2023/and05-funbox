package com.rpg.funbox.presentation

import android.content.Context
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import com.rpg.funbox.R
import com.rpg.funbox.databinding.ActivityMainBinding
import com.rpg.funbox.presentation.game.quiz.QuizViewModel
import com.rpg.funbox.presentation.map.GetGameDialog
import com.rpg.funbox.presentation.map.MapFragment
import com.rpg.funbox.presentation.map.MapFragmentDirections
import com.rpg.funbox.presentation.map.MapUiEvent
import com.rpg.funbox.presentation.map.MapViewModel
import com.rpg.funbox.presentation.map.MessageDialog
import com.rpg.funbox.presentation.setting.SettingViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val quizViewModel: QuizViewModel by viewModels()
    private val mapViewModel: MapViewModel by viewModels()
    private val settingViewModel: SettingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.ibSide.setOnClickListener { binding.drawerLayout.openDrawer(GravityCompat.START) }
        binding.navView.setNavigationItemSelectedListener { menuItem->
            when(menuItem.itemId){
                R.id.menu_map -> {settingViewModel.toMap()}
                R.id.menu_setting -> {mapViewModel.toSetting()}
            }
            binding.drawerLayout.closeDrawers()
            return@setNavigationItemSelectedListener false
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
}