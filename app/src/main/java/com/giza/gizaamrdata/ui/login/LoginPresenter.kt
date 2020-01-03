package com.giza.gizaamrdata.ui.login

import android.annotation.SuppressLint
import com.giza.gizaamrdata.data.local.UserPreferences
import com.giza.gizaamrdata.data.remote.RetrofitProvider
import com.giza.gizaamrdata.ui.base.BasePresenter
import com.giza.gizaamrdata.utils.Logger


/**
 * @author hossam.
 */
class LoginPresenter(view: LoginContract.View) : BasePresenter<LoginContract.View>(view), LoginContract.Presenter {

    @SuppressLint("CheckResult")
    override fun login(userName: String, password: String) {
        RetrofitProvider.login(userName, password)
            .subscribe({ response ->
                Logger.e("Login request success with response code ${response.code()} body ${response.body()?.status} ${response.body()?.status_message}")
//                UserPreferences.header = it.headers().get("amrtoken")
                UserPreferences.header = response.body()?.status_message
                response.body()?.api_url?.let {
                    val url = it.replace("//", "/")
                    UserPreferences.baseUrl = url
                }
                UserPreferences.loggedIn = true
                UserPreferences.userName = userName
                Logger.e("Login request success with headers ${response.headers()}")
                navigateToHome()
            }, {
                Logger.e("Login request has been failed code due to ${it.message}")
                Logger.e("Login request has been failed base url  RetrofitProvider ${RetrofitProvider.BASE_URL}")
                Logger.e("Login request has been failed base url  UserPreferences ${UserPreferences.baseUrl}")
                this.view.get()?.showToast("failed due to ${it.message}")
            })
    }


    override fun navigateToHome() {
        view.get()?.goToHome()
    }
}