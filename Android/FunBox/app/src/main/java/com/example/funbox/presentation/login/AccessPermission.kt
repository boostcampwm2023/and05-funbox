package com.example.funbox.presentation.login

import android.Manifest

object AccessPermission {
    val profilePermissionList = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
}