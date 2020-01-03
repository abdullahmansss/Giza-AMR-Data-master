package com.giza.gizaamrdata.ui.setting

import com.giza.gizaamrdata.ui.base.BasePresenter


/**
 * @author hossam.
 */
class SettingPresenter(view: SettingContract.View) : BasePresenter<SettingContract.View>(view),
    SettingContract.Presenter {

    override fun updateBaseUrl(baseUrl: String) {
        view.get()?.updateBaseUrl(baseUrl)
    }
}