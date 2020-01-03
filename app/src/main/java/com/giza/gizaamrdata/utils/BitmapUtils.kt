package com.giza.gizaamrdata.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.View


/**
 * @author hossam.
 */
object BitmapUtils {
    //create bitmap from the ScrollView
    fun getBitmapFromView(view: View, height: Int, width: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val bgDrawable = view.background
        if (bgDrawable != null)
            bgDrawable.draw(canvas)
        else
            canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return bitmap
    }

    fun getBitmap(myBitmap: Bitmap): Bitmap {
        var outWidth: Int
        val outHeight: Int
        val inWidth = myBitmap.width
        val inHeight = myBitmap.height
        outWidth = inWidth
        outHeight = inHeight
        if (inWidth > inHeight) {
//            outWidth = maxSize
//            outHeight = inHeight * maxSize / inWidth
        } else {
            outWidth = (inWidth / 1.2).toInt()
        }

        return Bitmap.createScaledBitmap(myBitmap, outWidth, outHeight, false)
    }
}
