package com.giza.gizaamrdata.ui.meterdetails

import com.giza.gizaamrdata.ui.base.BasePresenter


/**
 * @author hossam.
 */
class MeterDetailsPresenter(view: MeterDetailsContract.View) : BasePresenter<MeterDetailsContract.View>(view), MeterDetailsContract.Presenter {
    override fun navigateToWizard() {
        view.get()?.goToWizard()
    }
}