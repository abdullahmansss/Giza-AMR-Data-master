package com.giza.gizaamrdata.ui.wizard

import android.annotation.SuppressLint
import com.giza.gizaamrdata.data.local.UserPreferences
import com.giza.gizaamrdata.data.local.database.MetersDatabase
import com.giza.gizaamrdata.data.remote.RetrofitProvider
import com.giza.gizaamrdata.models.Meter
import com.giza.gizaamrdata.ui.base.BasePresenter
import com.giza.gizaamrdata.utils.Logger
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
class WizardPresenter(view: WizardContract.View) : BasePresenter<WizardContract.View>(view), WizardContract.Presenter {

    override fun addNewMeterToDb() {
        insertToDB(MeterModel.meter)
    }

    private fun insertToDB(vararg meters: Meter) {
        GlobalScope.launch(Dispatchers.IO) {
            MetersDatabase.getInstance().metersDao().insert(*meters).subscribe({
                UserPreferences.lastInsertedRowId = meters.last().id
                Logger.d("DB insertion succeeded ${meters.size} meters inserted")
                uploadMeters(meters.toMutableList())
                uploadImages(meters.toMutableList())
                MeterModel.destroy()
            }, {
                Logger.d("DB insertion failed  with error ${it.message}")
            })
        }
    }

    @SuppressLint("CheckResult")
    override fun uploadMeters(meters: MutableList<Meter>) {
        RetrofitProvider.uploadMeters(meters).subscribe(
            { it ->
                Logger.d("UploadMeters succeeded ${meters.size} meters uploaded")
                GlobalScope.launch(Dispatchers.IO) {
                    MetersDatabase.getInstance().metersDao().delete(*meters.toTypedArray())
                }
            }, {
                Logger.d("UploadMeters failed  with error ${it.message}")
            })
    }

    @SuppressLint("CheckResult")
    override fun uploadImages(meters: MutableList<Meter>) {
        for (m in meters) {
            val urls = m.urls.filterNot { it.startsWith("http://") || it.startsWith("https://") }
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
                    var deleted = false
                    try {
                        deleted = file.delete()
                        Logger.d("image ${file.path} isDeleted $deleted")
                    } catch (e : Exception) {
                        Logger.d("image ${file.path} isDeleted $deleted")
                    }
                }, {
                    Logger.d("uploadImages failed  with error ${it.message}")
                })
            }
        }
    }

    override fun setImagesUpdated(updated: Boolean) {
        MeterModel.isImagesUpdated = updated
    }

    override fun isImagesUpdated(): Boolean {
        return MeterModel.isImagesUpdated
    }

    override fun isMeterInfoCompleted(): Boolean {
        return MeterModel.completed
    }


}