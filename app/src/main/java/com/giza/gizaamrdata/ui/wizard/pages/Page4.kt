package com.giza.gizaamrdata.ui.wizard.pages


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat
import androidx.fragment.app.Fragment
import com.giza.gizaamrdata.R
import com.giza.gizaamrdata.ui.wizard.MeterModel
import com.giza.gizaamrdata.ui.wizard.WizardFragment
import com.giza.gizaamrdata.utils.Logger
import com.giza.gizaamrdata.utils.Rx2Bus
import com.giza.gizaamrdata.utils.RxEvents
import com.giza.gizaamrdata.utils.extensions.hide
import com.giza.gizaamrdata.utils.extensions.show
import com.google.android.material.snackbar.Snackbar
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_page4.*


class Page4 : Fragment() {
    private lateinit var wizardDisposable: Disposable

    lateinit var locationManager: LocationManager
    lateinit var locationListener: LocationListener

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 100
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_page4, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        retainInstance = true
        createLocationListener()
        onRequestLocationClicked()
    }

    private fun onRequestLocationClicked() {
        btnRequestLocation.setOnClickListener {
            progressbar.show()
            startLocationUpdates()
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {

        locationManager = this.activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (LocationManagerCompat.isLocationEnabled(locationManager)) {
            progressbar?.show()
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0f, locationListener)
        } else {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    private fun createLocationListener() {
        locationListener = object : LocationListener {

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

            override fun onProviderEnabled(provider: String?) {}

            override fun onProviderDisabled(provider: String?) {}

            override fun onLocationChanged(location: Location?) {
                if (location != null) {
                    updateMeterLocationIfMoreAccurate(location)
                    progressbar?.hide()
                }
            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
            } else {
                // fragmentContainer
                val snackbar = Snackbar.make(
                    this.requireView(),
                    getString(com.giza.gizaamrdata.R.string.permission_denied_location),
                    Snackbar.LENGTH_INDEFINITE
                )
                snackbar.setAction("Settings") {
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", "com.giza.gizaamrdata", null)
                    intent.data = uri
                    startActivity(intent)
                }
                snackbar.show()
            }
        }
    }

    object PermissionHelper {

        fun requestLocationPermission(
            activity: Activity,
            fragment: Fragment,
            requestCode: Int,
            message: String
        ): Boolean {
            return requestPermission(
                activity,
                fragment,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                requestCode,
                message
            )
        }

        fun checkLocationPermission(activity: Context): Boolean {
            return checkPermission(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }

        fun checkPermission(activity: Context?, permissions: Array<String>): Boolean {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(activity!!, permission) != PackageManager.PERMISSION_GRANTED)
                    return false
            }

            return true
        }

        fun requestPermission(
            activity: Activity,
            fragment: Fragment?,
            permissions: Array<String>,
            requestCode: Int,
            message: String
        ): Boolean {
            var context: Context? = activity
            if (fragment != null) {
                context = fragment.context
            }
            val isAllow = checkPermission(context, permissions)
            if (!isAllow) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        activity,
                        permissions[0]
                    )
                ) {
                    val dialog = AlertDialog.Builder(context)
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok) { dialog, which ->
                            if (fragment == null) {
                                ActivityCompat.requestPermissions(
                                    activity,
                                    permissions,
                                    requestCode
                                )
                            } else {
                                fragment.requestPermissions(permissions, requestCode)
                            }
                        }
                        .create()
                    dialog.show()
                } else {
                    if (fragment == null) {
                        ActivityCompat.requestPermissions(
                            activity,
                            permissions,
                            requestCode
                        )
                    } else {
                        fragment.requestPermissions(permissions, requestCode)
                    }
                }

                return false
            } else {
                return true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Logger.d("Page4 onResume")
        updateViewsWithExistedMeterData()
        updateNext()
        wizardDisposable = Rx2Bus.listen(RxEvents.Wizard::class.java).subscribe { wizardEvent ->
            if (wizardEvent.name == RxEvents.Wizard.PAGE4.name) {
                Logger.d("Page4  wizardDisposable ${wizardEvent.name} ")
                updateNext()
            }
        }
    }

    private fun updateNext() {
        if (!WizardFragment.openedForEdit) {
            try {
                if (PermissionHelper.requestLocationPermission(
                        this.requireActivity(), this, PERMISSION_REQUEST_ACCESS_FINE_LOCATION,
                        getString(R.string.permission_message_location)
                    )
                ) {
                    startLocationUpdates()
                }
                updateData()
            } catch (e: Exception) {
                Logger.e(e.message.toString())
                Rx2Bus.send(RxEvents.Wizard.DE_ACTIVATE_NEXT)
            }
        }
    }

    private fun updateData() {
        Logger.d("Page4 updateData -> trying to update location")
        if (MeterModel.meter.location.latitude != 0.0) {
            updateUi()
            Logger.d("Page4 updateData -> accuracy: ${MeterModel.meter.location.accuracy}")
        } else {
            updateNextButton()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::locationManager.isInitialized) {
            locationManager.removeUpdates(locationListener)
        }
    }

    private fun updateMeterLocationIfMoreAccurate(location: Location) {
        if (!WizardFragment.openedForEdit) {
            if (MeterModel.meter.location.latitude == 0.0) {
                updateMeterLocation(location)
            } else if (MeterModel.meter.location.accuracy > location.accuracy) {
                // if the new accuracy better than the recorded one update
                updateMeterLocation(location)
            } else {
                updateUi()
            }
        }
    }

    private fun updateMeterLocation(location: Location) {
        MeterModel.meter.location.latitude = location.latitude
        MeterModel.meter.location.longitude = location.longitude
        MeterModel.meter.location.altitude = location.altitude
        MeterModel.meter.location.accuracy = location.accuracy
        updateUi()
    }

    private fun updateUi() {
        txtLat?.text = MeterModel.meter.location.latitude.toString()
        txtLong?.text = MeterModel.meter.location.longitude.toString()
        txtAccuracy?.text = MeterModel.meter.location.accuracy.toString()
        updateNextButton()
    }

    private fun updateNextButton() {
        if (MeterModel.pageId == 3) {
            if (MeterModel.meter.location.accuracy <= 20.0) {
                Rx2Bus.send(RxEvents.Wizard.ACTIVATE_NEXT)
            } else {
                Rx2Bus.send(RxEvents.Wizard.DE_ACTIVATE_NEXT)
            }
        }
    }

    private fun updateViewsWithExistedMeterData() {
        if (WizardFragment.openedForEdit) {
            txtLat.text = MeterModel.meter.location.latitude.toString()
            txtLong.text = MeterModel.meter.location.longitude.toString()
            txtAccuracy.text = MeterModel.meter.location.accuracy.toString()
            progressbar?.hide()
        }
    }
}
