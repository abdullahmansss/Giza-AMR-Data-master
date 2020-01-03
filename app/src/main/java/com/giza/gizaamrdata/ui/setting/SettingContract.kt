package com.giza.gizaamrdata.ui.setting

import com.giza.gizaamrdata.ui.base.BaseContract

/**
 * @author hossam.
 */
class SettingContract : BaseContract {
    interface View : BaseContract.View<androidx.fragment.app.Fragment> {
        fun onUpdateClicked()
        fun updateBaseUrl(baseUrl: String)
        fun setHint()
        fun updateVersionNumber()
        fun updateUser()
        fun onlogoutClicked()
    }

    interface Presenter : BaseContract.Presenter {
        fun updateBaseUrl(baseUrl: String)
    }
}