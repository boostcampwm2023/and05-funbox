package com.rpg.funbox.presentation.map

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.rpg.funbox.databinding.MapProfileBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MapProfile @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val layoutInflater = LayoutInflater.from(context)
    private val binding: MapProfileBinding = MapProfileBinding.inflate(layoutInflater, this, true)

    @RequiresApi(Build.VERSION_CODES.P)
    fun setProfile(image: Bitmap) {
        binding.ivProfile.setImageBitmap(image)
        binding.ivProfile.invalidate()
    }

    fun setName(text: String) {
        binding.tvProfileName.text = text
    }

    fun setMsg(text: String) {
        binding.tvProfileMsg.text = text
    }

}