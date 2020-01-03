package com.giza.gizaamrdata.ui.wizard.pages


import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.giza.gizaamrdata.R
import com.giza.gizaamrdata.ui.wizard.MeterModel
import com.giza.gizaamrdata.ui.wizard.WizardFragment
import com.giza.gizaamrdata.utils.Logger
import com.giza.gizaamrdata.utils.Rx2Bus
import com.giza.gizaamrdata.utils.RxEvents
import com.giza.gizaamrdata.utils.extensions.onChange
import com.google.android.gms.vision.barcode.Barcode
import com.notbytes.barcode_reader.BarcodeReaderActivity
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_page1.*


class Page1 : Fragment() {

    private lateinit var wizardDisposable: Disposable

    companion object {
        const val BARCODE_READER_ACTIVITY_REQUEST = 11
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_page1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        retainInstance = true
        onScanClicked()
    }

    private fun onScanClicked() {
        btnScan.setOnClickListener {
            scan()
        }

        edtMeterNumber.onChange { meterNumberText ->
            MeterModel.meter.number = meterNumberText
            if (meterNumberText.isEmpty()) {
                Rx2Bus.send(RxEvents.Wizard.DE_ACTIVATE_NEXT)
            } else {
                edtAddress.text?.let { addressText ->
                    if (addressText.isNotEmpty())
                        Rx2Bus.send(RxEvents.Wizard.ACTIVATE_NEXT)
                }
            }
        }

        edtAddress.onChange { addressText ->
            MeterModel.meter.building.address?.place = addressText
            if (addressText.isEmpty()) {
                Rx2Bus.send(RxEvents.Wizard.DE_ACTIVATE_NEXT)
            } else {
                edtMeterNumber.text?.let { meterNumberText ->
                    if (meterNumberText.isNotEmpty())
                        Rx2Bus.send(RxEvents.Wizard.ACTIVATE_NEXT)
                }
            }
        }
    }

    private fun scan() {
        val launchIntent = BarcodeReaderActivity.getLaunchIntent(this.requireContext(), true, false)
        startActivityForResult(launchIntent, BARCODE_READER_ACTIVITY_REQUEST)
    }

    override fun onResume() {
        super.onResume()
        Logger.d("Page1 onResume")
        updateViewsWithExistedMeterData()
        wizardDisposable = Rx2Bus.listen(RxEvents.Wizard::class.java).subscribe { wizardEvent ->
            if (wizardEvent.name == RxEvents.Wizard.PAGE1.name) {
                Logger.d("wizardDisposable invoked PAGE1")
                updateNext()
            }
        }
    }

    private fun updateNext() {
        try {
            if (edtMeterNumber.text.isNullOrEmpty() || edtAddress.text.isNullOrEmpty())
                Rx2Bus.send(RxEvents.Wizard.DE_ACTIVATE_NEXT)
            else {
                Rx2Bus.send(RxEvents.Wizard.ACTIVATE_NEXT)
            }
        } catch (e: Exception) {
            Logger.e(e.message.toString())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BARCODE_READER_ACTIVITY_REQUEST) {
            if (resultCode == RESULT_OK && data != null) {
                val barcode = data.getParcelableExtra(BarcodeReaderActivity.KEY_CAPTURED_BARCODE) as Barcode
                edtMeterNumber.setText(barcode.rawValue)
            }
            if (resultCode == RESULT_CANCELED) {
                //handle cancel
            }
        }
    }

    private fun updateViewsWithExistedMeterData() {
        if (WizardFragment.openedForEdit) {
            edtMeterNumber.setText(MeterModel.meter.number)
            edtAddress.setText(MeterModel.meter.building.address?.place)
        }
    }
}