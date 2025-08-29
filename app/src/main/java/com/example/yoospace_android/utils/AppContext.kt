package com.example.yoospace_android.utils


import android.app.Application
import android.content.Context
import com.example.yoospace_android.data.local.TokenManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AppContext : Application() {

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        val token = TokenManager.getAccessToken()
        if (token != null) {
            SocketManager.init("https://yoo-space.onrender.com", token)
        }
    }

    companion object {
        private var instance: AppContext? = null

        fun get(): Context {
            return instance!!.applicationContext
        }
    }
}
