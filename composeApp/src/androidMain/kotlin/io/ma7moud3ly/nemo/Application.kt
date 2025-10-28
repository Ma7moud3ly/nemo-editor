package io.ma7moud3ly.nemo

import android.app.Application

class AndroidApplication : Application() {
    override fun onCreate() {
        instance = this
        super.onCreate()
    }

    companion object {
        private var instance: AndroidApplication? = null
        fun getAppContext(): AndroidApplication? {
            return instance
        }
    }
}