package com.giza.gizaamrdata.ui.wizard.pages


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
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_page3.*

class Page3 : Fragment() {
    private lateinit var wizardDisposable: Disposable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_page3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        retainInstance = true

        edtOwnerName.onChange {
            MeterModel.meter.owner.name = it
        }
        edtOwnerPhone.onChange {
            MeterModel.meter.owner.phone = it
        }
        edtOwnerId.onChange {
            MeterModel.meter.owner.national_id = it
        }
        edtOldMeterNumber.onChange {
            MeterModel.meter.owner.old_meter_number = it
        }
        edtOldMeterReadings.onChange {
            MeterModel.meter.owner.old_meter_reading = it
        }
        edtElectricityMeterNumber.onChange {
            MeterModel.meter.owner.electricity_meter = it
        }
        edtCustom1.onChange {
            MeterModel.meter.owner.custom1 = it
        }
        edtCustom2.onChange {
            MeterModel.meter.owner.custom2 = it
        }
        edtCustom3.onChange {
            MeterModel.meter.owner.custom3 = it
        }
        edtCustom4.onChange {
            MeterModel.meter.owner.custom4 = it
        }
        edtCustom5.onChange {
            MeterModel.meter.owner.custom5 = it
        }
        edtCustom6.onChange {
            MeterModel.meter.owner.custom6 = it
        }
        edtCustom7.onChange {
            MeterModel.meter.owner.custom7 = it
        }
        edtCustom8.onChange {
            MeterModel.meter.owner.custom8 = it
        }
        edtCustom9.onChange {
            MeterModel.meter.owner.custom9 = it
        }
        edtCustom10.onChange {
            MeterModel.meter.owner.custom10 = it
        }
    }

    override fun onResume() {
        super.onResume()
        Logger.d("Page3 onResume")
        updateViewsWithExistedMeterData()
        wizardDisposable = Rx2Bus.listen(RxEvents.Wizard::class.java).subscribe { wizardEvent ->
            if (wizardEvent.name == RxEvents.Wizard.PAGE3.name) {
                Logger.d("wizardDisposable invoked PAGE3")
                Rx2Bus.send(RxEvents.Wizard.ACTIVATE_NEXT)
            }
        }
    }

    private fun updateViewsWithExistedMeterData()
    {
        if (WizardFragment.openedForEdit)
        {
            edtOwnerName.setText(MeterModel.meter.owner.name)
            edtOwnerId.setText(MeterModel.meter.owner.national_id)
            edtOwnerPhone.setText(MeterModel.meter.owner.phone)
            edtOwnerPhone.setText(MeterModel.meter.owner.phone)
            edtElectricityMeterNumber.setText(MeterModel.meter.owner.electricity_meter)
        }
    }
}
