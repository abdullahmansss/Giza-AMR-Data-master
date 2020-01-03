package com.giza.gizaamrdata.ui.wizard

import com.giza.gizaamrdata.models.Meter
import com.giza.gizaamrdata.ui.base.BaseContract

/**
 * @author hossam.
 */
class WizardContract : BaseContract {
    interface View : BaseContract.View<androidx.fragment.app.Fragment> {
        fun handelButtonsVisibility(pos: Int, max: Int)
    }

    interface Presenter : BaseContract.Presenter {
        fun addNewMeterToDb()
        fun isMeterInfoCompleted() : Boolean
        fun uploadMeters(meters: MutableList<Meter>)
        fun uploadImages(meters: MutableList<Meter>)
        fun isImagesUpdated() : Boolean
        fun setImagesUpdated(updated : Boolean)
    }
}