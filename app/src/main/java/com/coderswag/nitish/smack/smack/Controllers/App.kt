package com.coderswag.nitish.smack.smack.Controllers

import android.app.Application
import com.android.volley.toolbox.Volley
import com.coderswag.nitish.smack.smack.Utilities.SharedPrefs

class App: Application() {

    companion object {
        lateinit var prefs: SharedPrefs
    }

    override fun onCreate() {
        super.onCreate()
        prefs = SharedPrefs(applicationContext)
    }
}