package com.giza.gizaamrdata.ui.wizard.pages


import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import com.giza.gizaamrdata.GizaApp
import com.giza.gizaamrdata.ui.wizard.MeterModel
import com.giza.gizaamrdata.ui.wizard.WizardFragment.Companion.openedForEdit
import com.giza.gizaamrdata.utils.Logger
import com.giza.gizaamrdata.utils.Rx2Bus
import com.giza.gizaamrdata.utils.RxEvents
import com.squareup.picasso.Picasso
import id.zelory.compressor.Compressor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_page5.*
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.MediaFile
import pl.aprilapps.easyphotopicker.MediaSource
import java.io.File


class Page5 : Fragment() {
    private val PICK_IMAGE1 = 1
    private val CAPTURE_IMAGE1 = 5
    private val PICK_IMAGE2 = 2
    private val CAPTURE_IMAGE2 = 6
    private val PICK_IMAGE3 = 3
    private val CAPTURE_IMAGE3 = 7
    private var currentRequest = 0
    private var PERMISSION_CODE = 100
    private var PERMISSION_CODE_CAM = 200
    private lateinit var easyImage: EasyImage
    private lateinit var wizardDisposable: Disposable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(com.giza.gizaamrdata.R.layout.fragment_page5, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        retainInstance = true
        easyImage = EasyImage.Builder(this.requireContext())
            .setCopyImagesToPublicGalleryFolder(false)
            .allowMultiple(false)
            .build()
        addOnClickListeners()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Logger.d("onActivityResult done")
        if (this.isAdded && this.requireActivity() != null) {
            easyImage.handleActivityResult(
                requestCode,
                resultCode,
                data,
                this.requireActivity(),
                object : DefaultCallback() {
                    @SuppressLint("CheckResult")
                    override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                        Logger.d("onActivityResult onMediaFilesPicked ${imageFiles.size}")
                        Logger.d("onActivityResult onMediaFilesPicked path: ${imageFiles[0].file.path} name: ${imageFiles[0].file.name} ")

                        Compressor(GizaApp.instance.applicationContext)
                            .setQuality(90)
                            .setMaxWidth(1224)
                            .setMaxHeight(1632)
                            .compressToFileAsFlowable(imageFiles[0].file)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ file ->
                                loadImageIntoImageView(file, getImageViewBasedOnRequestCode(currentRequest))
                                updateMeterUrls(file.path)
                            }, { throwable ->
                                throwable.printStackTrace()
                            })
                    }

                    override fun onImagePickerError(@NonNull error: Throwable, @NonNull source: MediaSource) {
                        //Some error handling
                        error.printStackTrace()
                        Logger.e("onActivityResult onImagePickerError failed images")
                    }

                    override fun onCanceled(@NonNull source: MediaSource) {
                        //Not necessary to remove any files manually anymore
                        Logger.e("onActivityResult onCanceled failed images")
                    }
                })
        }

    }

    //handle requested permission result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    easyImage.openGallery(this)
                } else {
                    //permission from popup denied
                    Toast.makeText(this.requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            PERMISSION_CODE_CAM -> {
                if (grantResults.isNotEmpty() && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    //permission from popup granted
                    easyImage.openCameraForImage(this)
                } else {
                    //permission from popup denied
                    Toast.makeText(this.requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addOnClickListeners() {
        btnPick1.setOnClickListener {
            checkPermission()
            currentRequest = PICK_IMAGE1
        }
        btnPick2.setOnClickListener {
            checkPermission()
            currentRequest = PICK_IMAGE2
        }
        btnPick3.setOnClickListener {
            checkPermission()
            currentRequest = PICK_IMAGE3
        }
        btnCam1.setOnClickListener {
            currentRequest = CAPTURE_IMAGE1
            checkPermissionCam()
        }
        btnCam2.setOnClickListener {
            currentRequest = CAPTURE_IMAGE2
            checkPermissionCam()
        }
        btnCam3.setOnClickListener {
            currentRequest = CAPTURE_IMAGE3
            checkPermissionCam()
        }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.parentFragment?.requireActivity()?.checkSelfPermission(READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                //permission denied
                val permissions = arrayOf(READ_EXTERNAL_STORAGE)
                //show popup to request runtime permission
                requestPermissions(permissions, PERMISSION_CODE)
            } else {
                //permission already granted
                easyImage.openGallery(this)
            }
        } else {
            //system OS is < Marshmallow
            easyImage.openGallery(this)
        }
    }

    private fun checkPermissionCam() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.parentFragment?.requireActivity()?.checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                || this.parentFragment?.requireActivity()?.checkSelfPermission(CAMERA) == PackageManager.PERMISSION_DENIED
            ) {
                //permission denied
                val permissions = arrayOf(WRITE_EXTERNAL_STORAGE, CAMERA)
                //show popup to request runtime permission
                requestPermissions(permissions, PERMISSION_CODE_CAM)
            } else {
                //permission already granted
                easyImage.openCameraForImage(this)
            }
        } else {
            //system OS is < Marshmallow
            easyImage.openCameraForImage(this)
        }
    }


    private fun getImageViewBasedOnRequestCode(code: Int): ImageView {
        return when (code) {
            CAPTURE_IMAGE1, PICK_IMAGE1 -> img1
            CAPTURE_IMAGE2, PICK_IMAGE2 -> img2
            CAPTURE_IMAGE3, PICK_IMAGE3 -> img3
            else -> img1
        }
    }

    private fun getUrlIndexBasedOnRequestCode(code: Int): Int {
        return when (code) {
            CAPTURE_IMAGE1, PICK_IMAGE1 -> 0
            CAPTURE_IMAGE2, PICK_IMAGE2 -> 1
            CAPTURE_IMAGE3, PICK_IMAGE3 -> 2
            else -> 0
        }
    }


    override fun onResume() {
        super.onResume()
        Logger.d("Page5 onResume")
        updateViewsWithExistedMeterData()
        wizardDisposable = Rx2Bus.listen(RxEvents.Wizard::class.java).subscribe { wizardEvent ->
            if (wizardEvent.name == RxEvents.Wizard.PAGE5.name) {
                Logger.d("wizardDisposable invoked PAGE5")
                Rx2Bus.send(RxEvents.Wizard.ACTIVATE_NEXT)
            }
        }
    }

    private fun updateViewsWithExistedMeterData() {
        if (openedForEdit) {
            MeterModel.meter.urls.sort()
            MeterModel.meter.urls.forEachIndexed { index, s ->
                loadImageIntoImageView(s, getImageViewBasedOnRequestCode(index + 1))
            }
        }
    }

    private fun loadImageIntoImageView(imageUri: String, imageView: ImageView) {
        Picasso.get()
            .load(imageUri)
            .fit()
            .centerCrop()
            .into(imageView)
    }

    private fun loadImageIntoImageView(imageFile: File, imageView: ImageView) {
        Picasso.get()
            .load(imageFile)
            .fit()
            .centerCrop()
            .into(imageView)
    }

    private fun updateMeterUrls(filePath: String) {
        MeterModel.isImagesUpdated = true
        if (openedForEdit) {
            MeterModel.meter.urls.sort()
        }
        if (MeterModel.meter.urls.getOrNull(getUrlIndexBasedOnRequestCode(currentRequest)) != null) {
            val tempUrl = MeterModel.meter.urls[getUrlIndexBasedOnRequestCode(currentRequest)]
            MeterModel.meter.urls[getUrlIndexBasedOnRequestCode(currentRequest)] =
                """$filePath old= $tempUrl"""
        } else {
            MeterModel.meter.urls.add(filePath)
        }
        Logger.d(MeterModel.meter.urls.joinToString())
    }
}

