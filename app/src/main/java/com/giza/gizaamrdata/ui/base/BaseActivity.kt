package com.giza.gizaamrdata.ui.base

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.giza.gizaamrdata.utils.LocaleManager

/**
 * @author hossam.
 */
abstract class BaseActivity : AppCompatActivity() {
    abstract fun getLayoutResourceId(): Int
    abstract fun getTitleResourceId(): String
    abstract fun init()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResourceId())

        title = getTitleResourceId()
        if (savedInstanceState == null)
            init()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleManager.setLocale(base))
    }
}