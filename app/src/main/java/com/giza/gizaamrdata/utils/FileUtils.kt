package com.giza.gizaamrdata.utils

import android.annotation.SuppressLint
import com.giza.gizaamrdata.ui.wizard.MeterModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.IOException


/**
 * @author hossam.
 */
object FileUtils {

    @SuppressLint("CheckResult")
    internal fun renameNewAddedFiles(filesToCopy: List<File>) {
        Logger.e("copyFilesInSeparateThread ${filesToCopy.joinToString()}")

        Observable.fromCallable {
            var i = 1
            for (fileToCopy in filesToCopy) {
                val filenameSplit = fileToCopy.name.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val extension = "." + filenameSplit[filenameSplit.size - 1]
                val filename = "IMG_${MeterModel.meter.number}_$i$extension"
                Logger.d("copyFilesInSeparateThread filename $filename")

                val dstFile = File(fileToCopy.parent, filename)

                try {
                    fileToCopy.renameTo(dstFile)
                    MeterModel.meter.urls[MeterModel.meter.urls.indexOf(fileToCopy.path)] = dstFile.path
                } catch (e: IOException) {
                    e.printStackTrace()
                    Logger.e("IO Error Image can't be renamed")
                }
                i++
            }
            true
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result ->
                if (result == true) {
                    Logger.d("images done")
                } else {
                    Logger.d("images failed")
                }
            }
    }


    @SuppressLint("CheckResult")
    internal fun renameFiles(filesToCopy: List<String>) {
        Logger.e("copyFilesInSeparateThread ${filesToCopy.joinToString()}")

        Observable.fromCallable {
            for (fileToCopy in filesToCopy) {
                val urlParts = fileToCopy.split(" old= ")
                val fileTobeRenamed = urlParts[0] //new file /easyImages/bla/bla/ie345343.jpg
                val oldFullFileName = urlParts[1] //old name  https://baseurl/IMG_123455_3.jpg
                val oldFileName = oldFullFileName.substringAfterLast("/")

                val physicalFile = File(fileTobeRenamed)
                val dstFile = File(physicalFile.parent, oldFileName)
                var renamed = false
                try {
                    physicalFile.renameTo(dstFile)
                    renamed = true
                } catch (e: IOException) {
                    e.printStackTrace()
                    Logger.e("IO Error Image can't be renamed")
                }
                if (renamed) {
                    Logger.d("File Renamed successfully")

                    if (MeterModel.meter.urls.isNotEmpty() && MeterModel.meter.urls.indexOf(fileToCopy) !=-1 ) {
                        MeterModel.meter.urls[MeterModel.meter.urls.indexOf(fileToCopy)] =
                            dstFile.path
                        Logger.d("Meter urls updated with the new renamed file path")
                    }
                }
                Logger.d("File renaming process has finished for the file: $fileToCopy")
            }

            true
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result ->
                if (result == true) {
                    Logger.d("images done")
                } else {
                    Logger.d("images failed")
                }
            }
    }
}