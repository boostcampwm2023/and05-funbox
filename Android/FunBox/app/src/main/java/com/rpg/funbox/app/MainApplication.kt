package com.rpg.funbox.app

import android.app.Application
import android.content.Context
import timber.log.Timber

class MainApplication : Application() {

    init {
        instance = this
    }

    override fun onCreate() {
        mySharedPreferences = MySharedPreferences()
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }

    companion object {
        lateinit var mySharedPreferences: MySharedPreferences
        var instance: MainApplication? = null

        fun myContext(): Context {
            return instance!!.applicationContext
        }
    }
}