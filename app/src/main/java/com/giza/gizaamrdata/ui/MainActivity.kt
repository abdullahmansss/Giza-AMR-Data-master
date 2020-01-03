package com.giza.gizaamrdata.ui

import android.content.Intent
import com.giza.gizaamrdata.R
import com.giza.gizaamrdata.data.local.UserPreferences
import com.giza.gizaamrdata.ui.authentication.AuthFragment
import com.giza.gizaamrdata.ui.base.BaseActivity
import com.giza.gizaamrdata.ui.home.HomeFragment
import com.giza.gizaamrdata.ui.login.LoginFragment
import com.giza.gizaamrdata.utils.Logger


class MainActivity : BaseActivity() {

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_main
    }

    override fun getTitleResourceId(): String {
        return getString(R.string.app_name)
    }

    override fun init() {
        when {
            UserPreferences.loggedIn ->
                NavigationManager.attachAsRoot(this, HomeFragment.newInstance(), NavigationManager.Tags.HOME
            )
            UserPreferences.authorized -> NavigationManager.attachAsRoot(
                this,
                LoginFragment.newInstance(),
                NavigationManager.Tags.LOGIN
            )
            else -> NavigationManager.attachAsRoot(
                this,
                AuthFragment.newInstance(),
                NavigationManager.Tags.AUTH
            )
        }
    }

    override fun onBackPressed() {
        NavigationManager.navigateBack(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Logger.d("onActivityResult MainActivity")
        val fragment = NavigationManager.getById(this.supportFragmentManager)
        fragment?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}
