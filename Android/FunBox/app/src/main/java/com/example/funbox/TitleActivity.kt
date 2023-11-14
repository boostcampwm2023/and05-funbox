package com.example.funbox

import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.example.funbox.databinding.ActivityTitleBinding

class TitleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTitleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_title)
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

            val inputMethodManager: InputMethodManager = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                it.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }
}