package com.giza.gizaamrdata.utils

import android.content.Context
import android.content.pm.PackageManager

/**
 * @author hossam.
 */
object DeviceUtils {

    fun getAppVersionName(context: Context): String {
        try {
            return context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return ""
    }
}