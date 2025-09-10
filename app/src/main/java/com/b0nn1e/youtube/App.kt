package com.b0nn1e.youtube

import android.app.Application

open class App :Application() {

    companion object{
        lateinit var app: App
    }

    override fun onCreate() {
        super.onCreate()
        app = this
    }

}