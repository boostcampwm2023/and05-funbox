package com.rpg.funbox.presentation

import android.app.Activity
import android.app.ActivityOptions
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.rpg.funbox.R
import timber.log.Timber

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

fun NavController.safeNavigate(action: NavDirections) {
    Timber.d("Current Destination: ${currentDestination}")
    currentDestination?.getAction(action.actionId)?.run {
        navigate(action)
    }
}