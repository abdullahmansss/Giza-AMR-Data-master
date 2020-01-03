package com.giza.gizaamrdata.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.giza.gizaamrdata.R
import com.giza.gizaamrdata.data.local.UserPreferences
import com.giza.gizaamrdata.ui.NavigationManager
import com.giza.gizaamrdata.ui.base.BaseFragment
import com.giza.gizaamrdata.ui.login.LoginFragment
import com.giza.gizaamrdata.utils.DeviceUtils
import com.giza.gizaamrdata.utils.extensions.putArgs
import kotlinx.android.synthetic.main.setting_fragment.*


/**
 * @author hossam.
 */
class SettingFragment : BaseFragment<SettingContract.Presenter>(), SettingContract.View {

    override val fragmentLayoutResourceId = R.layout.setting_fragment

    companion object {
        fun newInstance() = SettingFragment().putArgs {
            val args = Bundle()
            val fragment = SettingFragment()
            fragment.arguments = args
            return fragment
        }

    }

    override fun init(savedInstanceState: Bundle?) {
        presenter = SettingPresenter(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        retainInstance = true
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onUpdateClicked()
        setHint()
        updateVersionNumber()
        updateUser()
        onlogoutClicked()
    }

    override fun onUpdateClicked() {
        btnUpdate.setOnClickListener {
            if (edtBaseUrl.text.toString().trim().isNotEmpty()) {
                presenter.updateBaseUrl(edtBaseUrl.text.toString().trim())
            } else {
                showToast(getString(R.string.msg_empty_Field))
            }
        }

    }

    override fun updateBaseUrl(baseUrl: String) {
        UserPreferences.baseUrl = baseUrl
        NavigationManager.popBackStackImmediate(this.requireFragmentManager())
    }

    override fun setHint() {
        btnUpdate.hint = UserPreferences.baseUrl
    }

    override fun updateUser() {
        txtUserName.text = UserPreferences.userName
    }

    override fun updateVersionNumber() {
        txtVersion.text = getString(R.string.version,DeviceUtils.getAppVersionName(this.requireContext()))
    }

    override fun onlogoutClicked() {
        btnLogout.setOnClickListener {
            UserPreferences.loggedIn = false
            NavigationManager.attachAsRoot(
                this.requireActivity() as AppCompatActivity,
                LoginFragment.newInstance(),
                tag = NavigationManager.Tags.LOGIN
            )
        }
    }


}