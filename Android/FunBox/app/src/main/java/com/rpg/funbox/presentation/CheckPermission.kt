package com.rpg.funbox.presentation

import android.app.Activity
import android.content.pm.PackageManager

fun Activity.checkPermission(permissions: Array<String>): Boolean {
    return permissions.all { permission ->
        checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }
}