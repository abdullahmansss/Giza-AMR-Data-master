package com.giza.gizaamrdata.ui.wizard.pages


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import com.giza.gizaamrdata.R
import com.giza.gizaamrdata.ui.wizard.MeterModel
import com.giza.gizaamrdata.ui.wizard.WizardFragment
import com.giza.gizaamrdata.utils.Logger
import com.giza.gizaamrdata.utils.Rx2Bus
import com.giza.gizaamrdata.utils.RxEvents
import com.giza.gizaamrdata.utils.extensions.getId
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_page2.*
import java.util.*


class Page2 : Fragment() {
    private lateinit var wizardDisposable: Disposable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(com.giza.gizaamrdata.R.layout.fragment_page2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        retainInstance = true
        rgBuildingType.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = getView()?.findViewById(checkedId) as RadioButton
            MeterModel.meter.building.type = radioButton.text.toString()
            checkCompletion()
        }

        rgBuildingUsage.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = getView()?.findViewById(checkedId) as RadioButton
            MeterModel.meter.building.usage = radioButton.text.toString()
            checkCompletion()
        }

        rgMeterState.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = getView()?.findViewById(checkedId) as RadioButton
            MeterModel.meter.state = radioButton.text.toString()
            checkCompletion()
        }

        rgNotes.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = getView()?.findViewById(checkedId) as RadioButton
            val position = group.indexOfChild(radioButton)
            MeterModel.meter.building.notes = position.toString()
            checkCompletion()
        }

        rgMeterVendor.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = getView()?.findViewById(checkedId) as RadioButton
            MeterModel.meter.vendor = radioButton.text.toString()
            checkCompletion()
        }
        rgStreetType.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = getView()?.findViewById(checkedId) as RadioButton
            MeterModel.meter.building.street_type = radioButton.text.toString()
            checkCompletion()
        }

    }

    private fun checkCompletion() {
        if (completed()) {
            Rx2Bus.send(RxEvents.Wizard.ACTIVATE_NEXT)
        } else {
            Rx2Bus.send(RxEvents.Wizard.DE_ACTIVATE_NEXT)
        }
    }

    override fun onResume() {
        super.onResume()
        Logger.d("page 2 onResume")
        updateViewsWithExistedMeterData()
        updateNext()

        wizardDisposable = Rx2Bus.listen(RxEvents.Wizard::class.java).subscribe { wizardEvent ->
            if (wizardEvent.name == RxEvents.Wizard.PAGE2.name) {
                Logger.d("wizardDisposable invoked PAGE2")
                updateNext()
            }
        }
    }

    private fun updateNext() {
        try {
            if (!completed())
                Rx2Bus.send(RxEvents.Wizard.DE_ACTIVATE_NEXT)
            else {
                Rx2Bus.send(RxEvents.Wizard.ACTIVATE_NEXT)
            }
        } catch (e: Exception) {
            Logger.e(e.message.toString())
            Rx2Bus.send(RxEvents.Wizard.DE_ACTIVATE_NEXT)
        }
    }

    private fun completed(): Boolean {
        return !(rgBuildingType.checkedRadioButtonId == -1
                || rgBuildingUsage.checkedRadioButtonId == -1
                || rgMeterState.checkedRadioButtonId == -1
                || rgNotes.checkedRadioButtonId == -1
                || rgMeterVendor.checkedRadioButtonId == -1
                || rgStreetType.checkedRadioButtonId == -1)
    }

    private fun updateViewsWithExistedMeterData() {
        if (WizardFragment.openedForEdit) {
            checkRadioButtonIfTextEqualsValue(rgMeterVendor, MeterModel.meter.vendor)
            checkRadioButtonIfTextEqualsValue(rgMeterState, MeterModel.meter.state)
            checkRadioButtonIfTextEqualsValue(rgBuildingType, MeterModel.meter.building.type)
            checkRadioButtonIfTextEqualsValue(rgBuildingUsage, MeterModel.meter.building.usage)
            checkRadioButtonIfTextEqualsValue(rgStreetType, MeterModel.meter.building.street_type)
            checkRadioButtonIfPositionEqualsValue(rgNotes, MeterModel.meter.building.notes)
        }
    }

    private fun checkRadioButtonIfTextEqualsValue(radioGroup: RadioGroup, value: String) {
        for (i in 0 until radioGroup.childCount) {
            val opti = i + 1
            val list = getStringFromLocal((radioGroup.getChildAt(i) as RadioButton).tag as String + opti)
            if (list.indexOf(value) > -1) {
                (radioGroup.getChildAt(radioGroup.childCount - i - 1) as RadioButton).isChecked = true
                break
            }
        }
    }

    private fun getStringFromLocal(stringName: String): List<String> {
        val str = getString(R.string::class.java.getId(stringName))
        val en = String.format(Locale("en"), str)
        val ar = String.format(Locale("ar"), str)
        val list = mutableListOf<String>()
        list.add(en)
        list.add(ar)
        return list
    }


    private fun checkRadioButtonIfPositionEqualsValue(radioGroup: RadioGroup, value: String) {
        if (value.isBlank()) return
        (radioGroup.getChildAt(value.toInt()) as RadioButton).isChecked = true
    }


}
