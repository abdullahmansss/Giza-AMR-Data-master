package com.giza.gizaamrdata.data.local

import com.giza.gizaamrdata.GizaApp
import com.giza.gizaamrdata.data.remote.RetrofitProvider
import com.giza.gizaamrdata.utils.extensions.showToast
import kotlin.reflect.KProperty


/**
 * @author hossam.
 */
object UserPreferences : UsePacePreferences() {
    var header by stringPref(defaultValue = "")
    var baseUrl by stringPref(defaultValue = "http://www.purediagnosticseg.com/amrconfig/")
    var selectedLanguage by stringPref(defaultValue = "ar")
    var lastInsertedRowId by intPref(defaultValue = 0)
    var authorized by booleanPref(defaultValue = false)
    var loggedIn by booleanPref(defaultValue = false)
    var uuid by stringPref("")
    var userName by stringPref("")

    init {
        addListener(object : SharedPrefsListener {
            override fun onSharedPrefChanged(property: KProperty<*>) {
                com.giza.gizaamrdata.utils.Logger.d("onSharedPrefChanged called ${property.name}")
                if (property.name == "baseUrl") {
                    baseUrl?.let {
                        RetrofitProvider.updateRetrofit(baseUrl.toString())
                        GizaApp.instance.applicationContext.showToast("Base Url updated")
                    }
                }
            }
        })
    }
}