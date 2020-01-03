package com.giza.gizaamrdata.ui.home

import android.annotation.SuppressLint
import com.giza.gizaamrdata.GizaApp
import com.giza.gizaamrdata.data.local.database.MetersDatabase
import com.giza.gizaamrdata.data.remote.RetrofitProvider
import com.giza.gizaamrdata.models.Meter
import com.giza.gizaamrdata.ui.base.BasePresenter
import com.giza.gizaamrdata.utils.Logger
import com.giza.gizaamrdata.utils.extensions.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


/**
 * @author hossam.
 */
class HomePresenter(view: HomeContract.View) : BasePresenter<HomeContract.View>(view), HomeContract.Presenter {

    override fun navigateToAddNew() {
        view.get()?.goToRegister()
    }


    @SuppressLint("CheckResult")
    override fun uploadMeters() {
        GlobalScope.launch(Dispatchers.IO) {
            val meters = MetersDatabase.getInstance().metersDao().getMeters()
            RetrofitProvider.uploadMeters(meters.toMutableList()).subscribe(
                { it ->
                    Logger.d("UploadMeters succeeded ${meters.size} meters uploaded")
                    GlobalScope.launch(Dispatchers.IO) {
                        MetersDatabase.getInstance().metersDao().delete(*meters.toTypedArray())
                        GlobalScope.launch(Dispatchers.Main) {
                            GizaApp.instance.applicationContext.showToast("Done.")
                        }
                    }
                    uploadImages(meters.toMutableList())
                }, {
                    Logger.d("UploadMeters failed  with error ${it.message}")
                })
        }
    }

    @SuppressLint("CheckResult")
    override fun uploadImages(meters: MutableList<Meter>) {
        for (m in meters) {
            val urls = m.urls
            Logger.d("uploadImages started urls ${urls.joinToString()}")
            for (url in urls) {
                val file = File(url)
                // Create a request body with file and image media type
                val fileReqBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                val fileReqBody2 = RequestBody.create("text/*".toMediaTypeOrNull(), m.number)

                // Create MultipartBody.Part using file request-body,file name and part name
                val part = MultipartBody.Part.createFormData("thefile", file.name, fileReqBody)
                RetrofitProvider.uploadImages(part, fileReqBody2).subscribe({ it ->
                    Logger.d("uploadImages succeeded code ${it.code()} ${it.body()?.status_message} uploaded")
                }, {
                    Logger.d("uploadImages failed  with error ${it.message}")
                })
            }
        }
    }
}