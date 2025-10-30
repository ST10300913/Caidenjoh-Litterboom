package com.example.litterboom

import android.app.Application
import com.example.litterboom.data.api.ApiClient

class MyApplication : Application() {

    companion object {
        lateinit var instance: MyApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        ApiClient.init(this)
    }
}