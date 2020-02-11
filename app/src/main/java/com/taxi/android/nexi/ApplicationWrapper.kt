package com.taxi.android.nexi

import android.app.Application
import android.content.Context
class ApplicationWrapper : Application() {
    companion object {
        lateinit var INSTANCE: ApplicationWrapper

        lateinit var context: Context


    }


    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }




    fun restartApp() {
        System.exit(0)
    }
}