package com.giza.gizaamrdata.ui.login

import com.giza.gizaamrdata.ui.base.BaseContract

/**
 * @author hossam.
 */
class LoginContract : BaseContract {
    interface View : BaseContract.View<androidx.fragment.app.Fragment> {
        fun onLoginClicked()
        fun goToHome()
    }

    interface Presenter : BaseContract.Presenter {
        fun navigateToHome()
        fun login(userName :String,password: String)
    }
}