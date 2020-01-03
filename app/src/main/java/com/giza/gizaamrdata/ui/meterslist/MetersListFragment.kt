package com.giza.gizaamrdata.ui.meterslist

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
import android.text.InputType
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.giza.gizaamrdata.R
import com.giza.gizaamrdata.data.C
import com.giza.gizaamrdata.models.SearchMeterResultedObject
import com.giza.gizaamrdata.ui.NavigationManager
import com.giza.gizaamrdata.ui.base.BaseFragment
import com.giza.gizaamrdata.ui.meterdetails.MeterDetailsFragment
import com.giza.gizaamrdata.utils.KeyboardUtils
import com.giza.gizaamrdata.utils.extensions.hide
import com.giza.gizaamrdata.utils.extensions.onChange
import com.giza.gizaamrdata.utils.extensions.putArgs
import com.giza.gizaamrdata.utils.extensions.show
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.material.snackbar.Snackbar
import com.notbytes.barcode_reader.BarcodeReaderActivity
import kotlinx.android.synthetic.main.meterslist_fragment.*

/**
 * @author hossam.
 */
class MetersListFragment : BaseFragment<MetersListContract.Presenter>(), MetersListContract.View {
    lateinit var locationListener: LocationListener
    lateinit var locationManager: LocationManager
    var location = com.giza.gizaamrdata.models.Location()
    override fun showGPSButtons() {
        lytSearchGPS.show()
        when (searchBy) {
            C.SearchOptions.GPS10 -> radioButton1.isChecked = true
            C.SearchOptions.GPS20 -> radioButton2.isChecked = true
            C.SearchOptions.GPS50 -> radioButton3.isChecked = true
            else -> radioButton1.isChecked = true
        }
        onGPSRadioButtonChanged()
        prepareLocationStuff()
    }

    override fun showQRScanner() {
        lytSearch.show()
        btnScan.show()
        scan()
        btnScan.setOnClickListener {
            scan()
        }
        txtInputEditText.onChange {
            presenter.loadByNumber(it)
        }
    }

    override fun showNumberInput() {
        lytSearch.show()
        txtInputEditText.inputType = InputType.TYPE_CLASS_NUMBER
        txtInputEditText.requestFocus()
        KeyboardUtils.showKeyboard(txtInputEditText)
        showKeyboard(requireActivity())
        txtInputEditText.onChange {
            presenter.loadByNumber(it)
        }
    }

    override fun showNameInput() {
        lytSearch.show()
        txtSearchTitle.text = getString(R.string.searchByOwner)
        txtInputEditText.inputType = InputType.TYPE_CLASS_TEXT
        txtInputEditText.requestFocus()
        KeyboardUtils.showKeyboard(txtInputEditText)
        txtInputEditText.onChange {
            presenter.loadByOwnerData(it)
        }
    }

    override fun showResults() {

    }

    override fun onUpdateQuery() {

    }

    private lateinit var adapter: MetersAdapter
    private lateinit var linearLayoutManager: androidx.recyclerview.widget.LinearLayoutManager
    override val fragmentLayoutResourceId = R.layout.meterslist_fragment
    var searchBy: C.SearchOptions = C.SearchOptions.NUMBER
    override fun init(savedInstanceState: Bundle?) {
        presenter = MetersListPresenter(this)
        searchBy = C.SearchOptions.values()[this.arguments?.getInt("H") ?: 0]
    }

    companion object {
        fun newInstance(searchOptions: C.SearchOptions) = MetersListFragment().putArgs {
            val args = Bundle()
            args.putInt("H", searchOptions.ordinal)
            val fragment = MetersListFragment()
            fragment.arguments = args
            return fragment
        }

        private const val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 200
        const val BARCODE_READER_ACTIVITY_REQUEST = 1111
    }

    private fun setUpRecyclerView() {
        adapter = MetersAdapter(
            context, presenter as MetersListPresenter
        ) { itemClick: SearchMeterResultedObject -> onItemClick(itemClick) }

        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(20)
        recyclerView.isDrawingCacheEnabled = true
        linearLayoutManager = LinearLayoutManager(this.context)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter
    }

    private fun onItemClick(meter: SearchMeterResultedObject) {
        NavigationManager.attachWithStack(
            this.requireActivity() as AppCompatActivity,
            MeterDetailsFragment.newInstance(meter),
            tag = NavigationManager.Tags.METER_DETAILS
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpRecyclerView()
        setSwipeRefreshListeners()
        presenter.startSearch(searchBy)
        onRequestLocationClicked()
    }

    private fun prepareLocationStuff() {
        createLocationListener()
        startLocationUpdates()
    }

    private fun setSwipeRefreshListeners() {
        swipeRefreshLayout.setOnRefreshListener {
            when (searchBy) {
                C.SearchOptions.GPS10, C.SearchOptions.GPS20, C.SearchOptions.GPS50 ->
                    presenter.loadByGPS(searchBy, location)
                C.SearchOptions.NUMBER, C.SearchOptions.QR -> presenter.loadByNumber(txtInputEditText.text.toString().trim())
                C.SearchOptions.NAME, C.SearchOptions.NATIONAL_ID -> presenter.loadByOwnerData(txtInputEditText.text.toString().trim())
            }
        }
    }

    override fun updateData() {
        adapter.notifyDataSetChanged()
    }

    override fun onGPSRadioButtonChanged() {
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (group?.findViewById(group.checkedRadioButtonId) as? RadioButton) {
                radioButton1 -> {
                    searchBy = C.SearchOptions.GPS10
                }
                radioButton2 -> {
                    searchBy = C.SearchOptions.GPS20
                }
                radioButton3 -> {
                    searchBy = C.SearchOptions.GPS50
                }
            }
            presenter.loadByGPS(searchBy, location)
        }
    }

    private fun scan() {
        val launchIntent = BarcodeReaderActivity.getLaunchIntent(this.requireContext(), true, false)
        startActivityForResult(launchIntent, BARCODE_READER_ACTIVITY_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BARCODE_READER_ACTIVITY_REQUEST) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val barcode = data.getParcelableExtra(BarcodeReaderActivity.KEY_CAPTURED_BARCODE) as Barcode
                txtInputEditText?.setText(barcode.rawValue.trim())
                presenter.loadByNumber(barcode.rawValue.trim())
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //handle cancel
            }
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

    private fun updateMeterLocationIfMoreAccurate(location: Location) {
        if (location.latitude == 0.0) {
            updateMeterLocation(location)
        } else if (this.location.accuracy > location.accuracy) {
            // if the new accuracy better than the recorded one update
            updateMeterLocation(location)
        } else {
            updateUi()
        }
    }

    private fun updateMeterLocation(location: Location) {
        this.location.latitude = location.latitude
        this.location.longitude = location.longitude
        this.location.altitude = location.altitude
        this.location.accuracy = location.accuracy
        presenter.loadByGPS(searchBy, this.location)
        updateUi()
    }

    private fun updateUi() {
        txtLat?.text = this.location.latitude.toString()
        txtLong?.text = this.location.longitude.toString()
        txtAccuracy?.text = this.location.accuracy.toString()
    }

    private fun showKeyboard(activity: Activity) {
        val view = activity.currentFocus
        val methodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        assert(view != null)
        methodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun hideLoading() {
        swipeRefreshLayout.isRefreshing = false
    }

    override fun showLoading() {
        swipeRefreshLayout.isRefreshing = true
    }

    override fun onResume() {
        super.onResume()
        if (searchBy == C.SearchOptions.GPS10 || searchBy == C.SearchOptions.GPS20 || searchBy == C.SearchOptions.GPS50) {
            if (PermissionHelper.requestLocationPermission(
                    this.requireActivity() as Activity, this, PERMISSION_REQUEST_ACCESS_FINE_LOCATION,
                    getString(R.string.permission_message_location)
                )
            ) {
                startLocationUpdates()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.destroy()
    }
}