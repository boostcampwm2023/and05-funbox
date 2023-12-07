package com.rpg.funbox.presentation

import android.app.Activity
import android.app.ActivityOptions
import android.content.pm.PackageManager
import android.os.Bundle
import com.rpg.funbox.R

fun Activity.checkPermission(permissions: Array<String>): Boolean {
    return permissions.all { permission ->
        checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }
}

fun Activity.slideLeft(): Bundle {
    return ActivityOptions.makeCustomAnimation(this, R.anim.slide_left_enter, R.anim.slide_left_exit).toBundle()
}

fun Activity.fadeInOut(): Bundle {
    return ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out).toBundle()
}