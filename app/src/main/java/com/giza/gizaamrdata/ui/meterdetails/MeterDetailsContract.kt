package com.giza.gizaamrdata.ui.meterdetails

import com.giza.gizaamrdata.ui.base.BaseContract

/**
 * @author hossam.
 */
class MeterDetailsContract : BaseContract {
    interface View : BaseContract.View<androidx.fragment.app.Fragment> {
        fun onEditClicked()
        fun goToWizard()
        fun onBackClicked()
        fun populateDate()
    }

    interface Presenter : BaseContract.Presenter {
        fun navigateToWizard()
    }
}