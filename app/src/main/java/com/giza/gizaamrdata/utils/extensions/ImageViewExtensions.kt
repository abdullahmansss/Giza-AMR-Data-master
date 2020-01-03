package com.giza.gizaamrdata.utils.extensions

import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.giza.gizaamrdata.GizaApp

//private var BASE_URL = when (BuildConfig.DEBUG)

fun ImageView.setImage(imageUri: Uri) {
    Glide.with(GizaApp.instance.applicationContext).load(imageUri).into(this)
}

fun ImageView.setImage(imageUri: String) {
    Glide.with(GizaApp.instance.applicationContext).load(imageUri).into(this)
}

fun ImageView.setImage(imageUri: Uri?, placeholderResId: Int) {
    Glide.with(GizaApp.instance.applicationContext).load(imageUri).placeholder(placeholderResId).into(this)
}

fun ImageView.setImage(imageUri: String?, placeholderResId: Int) {
    Glide.with(GizaApp.instance.applicationContext).load(imageUri).placeholder(placeholderResId)
        .into(this)
}

fun ImageView.setImage(resId: Int) {
    this.setImageResource(resId)
}

fun ImageView.setImage(bitmap: Bitmap) {
    this.setImageBitmap(bitmap)
}