package com.rpg.funbox.presentation.setting

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter

object SettingBindingAdapter {
    @BindingAdapter("name")
    @JvmStatic fun setName(view: TextView, nameText: String){

    }

    @InverseBindingAdapter(attribute = "name", event = "textEvent")
    @JvmStatic fun getName(view: TextView):String{
        return view.text.toString()
    }

}