package com.giza.gizaamrdata.ui.authentication

import com.giza.gizaamrdata.ui.base.BasePresenter


/**
 * @author hossam.
 */
class AuthPresenter(view: AuthContract.View) : BasePresenter<AuthContract.View>(view), AuthContract.Presenter {

    override fun navigateToLogin() {
        view.get()?.goToLogin()
    }
}