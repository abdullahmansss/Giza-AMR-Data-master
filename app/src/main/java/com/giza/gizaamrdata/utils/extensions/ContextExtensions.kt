package com.giza.gizaamrdata.utils.extensions

import android.content.Context
import android.widget.Toast
import com.giza.gizaamrdata.R

/**
 * @author hossam.
 */
fun Context.showToast(text: CharSequence, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, text, duration).show()
}



inline fun <reified T: Class<R.string>> T.getId(resourceName: String): Int {
    return try {
        val idField = getDeclaredField (resourceName)
        idField.getInt(idField)
    } catch (e:Exception) {
        e.printStackTrace()
        -1
    }
}