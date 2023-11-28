package com.akkobana.coroutines

import android.app.Application
import com.akkobana.coroutines.di.AppComponent
import com.akkobana.coroutines.di.DaggerAppComponent

class MainApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder().application(this).build()

    }

    companion object {
        lateinit var appComponent: AppComponent
    }
}