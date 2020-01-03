package com.giza.gizaamrdata.ui.home

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.giza.gizaamrdata.R
import com.giza.gizaamrdata.data.C
import com.giza.gizaamrdata.data.local.database.MetersDatabase
import com.giza.gizaamrdata.ui.NavigationManager
import com.giza.gizaamrdata.ui.base.BaseFragment
import com.giza.gizaamrdata.ui.meterslist.MetersListFragment
import com.giza.gizaamrdata.ui.setting.SettingFragment
import com.giza.gizaamrdata.ui.wizard.WizardFragment
import com.giza.gizaamrdata.utils.DataUtils
import com.giza.gizaamrdata.utils.Logger
import com.giza.gizaamrdata.utils.Rx2Bus
import com.giza.gizaamrdata.utils.RxEvents
import com.giza.gizaamrdata.utils.extensions.putArgs
import com.giza.gizaamrdata.utils.extensions.showToast
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 * @author hossam.
 */
class HomeFragment : BaseFragment<HomeContract.Presenter>(), HomeContract.View {

    override val fragmentLayoutResourceId = R.layout.home_fragment
    private lateinit var backHomeDisposable : Disposable
    companion object {
        fun newInstance() = HomeFragment().putArgs {
            val args = Bundle()
            val fragment = HomeFragment()
            fragment.arguments = args
            return fragment
        }

        private const val PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 1
    }

    override fun init(savedInstanceState: Bundle?) {
        presenter = HomePresenter(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        retainInstance = true
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onAddNewClicked()
        onExportButtonClicked()
        onSync()
    }

    override fun onAddNewClicked() {
        button.setOnClickListener {
            presenter.navigateToAddNew()
            val currentDBPath = this.requireContext().getDatabasePath(C.Database.NAME).absolutePath
            Logger.d(currentDBPath)
            getMeter()
        }
    }

    fun onExportButtonClicked() {
        btnExport.setOnClickListener {
            requestPermissions()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Logger.d("onActivityResult Home")
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun getMeter()
    {
        GlobalScope.launch(Dispatchers.IO)
        {
            val meters = MetersDatabase.getInstance().metersDao().getMeters()
            for (m in meters) {
                Logger.d(
                    "Meter ID ${m.id} \n Meter number: ${m.number} \n Meter location Long : ${m.location.longitude}" +
                            "\n" +
                            " Meter location Lat : ${m.location.latitude} \n" +
                            " Meter location accuracy is ${m.location.accuracy} M"
                )
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    prepareTheCsvFile()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    this.requireContext().showToast(getString(R.string.permissionNotGrantedForGeneratingReport))
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun prepareTheCsvFile() {
        GlobalScope.launch(Dispatchers.IO) {
            DataUtils.exportMeterFile(
                MetersDatabase.getInstance().metersDao().getMeters(),
                this@HomeFragment.requireActivity()
            )
        }
    }

    private fun requestPermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this.requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this.requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this.requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                || ActivityCompat.shouldShowRequestPermissionRationale(
                    this.requireActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {

                val dialog = AlertDialog.Builder(context)
                    .setMessage(getString(R.string.storagePermissionExplanation))
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        ActivityCompat.requestPermissions(
                            this.requireActivity(),
                            arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ),
                            PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE
                        )
                    }
                    .create()
                dialog.show()

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this.requireActivity(),
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE
                )

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            prepareTheCsvFile()
        }
    }

    override fun onSync() {
        btnSync.setOnClickListener {
            this.requireContext().showToast("Uploading meters Please wait...")
            presenter.uploadMeters() }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_search -> {
            showSearchDialog()
            true
        }
        R.id.action_settings -> {
            navigateToSettings()
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun navigateToSettings() {
        NavigationManager.attachWithStack(
            this.requireActivity() as AppCompatActivity,
            SettingFragment.newInstance(),
            tag = NavigationManager.Tags.SETTING
        )
        hideSearchIcon()
    }

    override fun showSearchDialog() {
        MaterialDialog(this.requireContext()).show {
            title(R.string.search_by_dialog_title)
            listItems(R.array.search_by_array, waitForPositiveButton = false)
            { _, index, text ->
                goToSearch(C.SearchOptions.values()[index])
            }
        }
    }


    override fun goToRegister() {
        NavigationManager.attachWithStack(
            this.requireActivity() as AppCompatActivity,
            WizardFragment.newInstance(),
            tag = NavigationManager.Tags.WIZARD
        )
        hideSearchIcon()
    }

    override fun goToSearch(searchOptions: C.SearchOptions) {
        NavigationManager.attachWithStack(
            this.requireActivity() as AppCompatActivity,
            MetersListFragment.newInstance(searchOptions),
            tag = NavigationManager.Tags.SEARCH
        )
        hideSearchIcon()
    }

    override fun showSearchIcon() {
        setHasOptionsMenu(true)
    }

    override fun hideSearchIcon() {
        setHasOptionsMenu(false)
    }

    override fun onResume() {
        super.onResume()
        showSearchIcon()
        backHomeDisposable = Rx2Bus.listen(RxEvents.HomeUtils::class.java).subscribe { homeEvents ->
            if (homeEvents.name == RxEvents.HomeUtils.BackHome.name) {
                showSearchIcon()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        backHomeDisposable.dispose()
    }

}