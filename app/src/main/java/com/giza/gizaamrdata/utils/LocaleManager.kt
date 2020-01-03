package com.giza.gizaamrdata.utils

import android.content.Context
import android.content.res.Configuration
import com.giza.gizaamrdata.data.local.UserPreferences
import kotlinx.coroutines.runBlocking
import java.util.*


/**
 * @author hossam.
 */
object LocaleManager {

    const val LANGUAGE_ENGLISH = "en"
    const val LANGUAGE_ARABIC = "ar"

    private fun updateResources(context: Context, language: String): Context {
        var locale = Locale(language)
        if (language == LANGUAGE_ARABIC) {
            locale = Locale(language, "MA")
        }
        Locale.setDefault(locale)
        val resources = context.resources
        val configuration = Configuration(resources.configuration)
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }

    fun setLocale(c: Context): Context {
        return updateResources(c, UserPreferences.selectedLanguage ?: LANGUAGE_ENGLISH)
    }

    fun setNewLocale(c: Context, language: String): Context {
        runBlocking {
            UserPreferences.selectedLanguage = language
        }
        return updateResources(c, language)
    }

    fun getCurrentLocale() = Locale(UserPreferences.selectedLanguage)

}