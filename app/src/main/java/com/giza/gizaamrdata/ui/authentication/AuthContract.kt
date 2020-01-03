package com.giza.gizaamrdata.ui.authentication

import com.giza.gizaamrdata.ui.base.BaseContract

/**
 * @author hossam.
 */
class AuthContract : BaseContract {
    interface View : BaseContract.View<androidx.fragment.app.Fragment> {
        fun onLoginClicked()
        fun goToLogin()
    }

    interface Presenter : BaseContract.Presenter {
        fun navigateToLogin()
    }
}