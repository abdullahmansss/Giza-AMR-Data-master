package com.giza.gizaamrdata.utils

import android.util.Log
import com.giza.gizaamrdata.BuildConfig

class Logger {

    companion object {
        fun d(message: String) {
            log(Log.DEBUG, message)
        }

        fun e(message: String) {
            log(Log.ERROR, message)
        }

        private fun log(logPriority: Int, message: String) {
            val tag = getTag()
            Log.d(tag, message)
        }

        private fun getTag(): String? {
            val elements = Thread.currentThread().stackTrace
            val packageName = BuildConfig.APPLICATION_ID
            for (element in elements) {
                if (!element.className.contains(Logger::class.java.name) && element.className.contains(packageName)) {
                    return "${element.fileName.substring(0, element.fileName.indexOf("."))} => ${element.methodName}"
                }
            }
            return null
        }
    }
}