package com.giza.gizaamrdata

import android.content.Context
import android.content.res.Configuration
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.giza.gizaamrdata.data.local.UsePacePreferences
import com.giza.gizaamrdata.utils.LocaleManager
import com.giza.gizaamrdata.utils.Logger
import kotlinx.coroutines.runBlocking


/**
 * @author hossam.
 */

class GizaApp : MultiDexApplication() {

    companion object {
        lateinit var instance: GizaApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Logger.d("starting...")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleManager.setLocale(this)
    }

    override fun attachBaseContext(base: Context) {
        runBlocking { UsePacePreferences.init(base) }
        super.attachBaseContext(LocaleManager.setLocale(base))
        MultiDex.install(this.applicationContext)
    }

}