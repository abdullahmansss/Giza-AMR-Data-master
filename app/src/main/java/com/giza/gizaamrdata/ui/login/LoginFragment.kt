package com.giza.gizaamrdata.ui.login

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.giza.gizaamrdata.R
import com.giza.gizaamrdata.data.local.UserPreferences
import com.giza.gizaamrdata.ui.NavigationManager
import com.giza.gizaamrdata.ui.base.BaseFragment
import com.giza.gizaamrdata.ui.home.HomeFragment
import com.giza.gizaamrdata.utils.extensions.putArgs
import kotlinx.android.synthetic.main.login_fragment.*


/**
 * @author hossam.
 */
class LoginFragment : BaseFragment<LoginContract.Presenter>(), LoginContract.View {

    override val fragmentLayoutResourceId = R.layout.login_fragment

    companion object {
        fun newInstance() = LoginFragment().putArgs {
            val args = Bundle()
            val fragment = LoginFragment()
            fragment.arguments = args
            return fragment
        }

    }

    override fun init(savedInstanceState: Bundle?) {
        presenter = LoginPresenter(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        retainInstance = true
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onLoginClicked()
    }

    override fun onLoginClicked() {
        btnLogin.setOnClickListener {
            if (edtPassword.text.toString().trim().isNotEmpty()
                && edtUserName.text.toString().trim().isNotEmpty()
                && edtBaseUrl.text.toString().trim().isNotEmpty()
            ) {
                //set base url
                if (!Patterns.WEB_URL.matcher(edtBaseUrl.text.toString()).matches()) {
                    showToast(getString(R.string.msg_error_should_be_valid_url))
                } else if (!edtBaseUrl.text.toString().endsWith("/")) {
                    UserPreferences.baseUrl = edtBaseUrl.text.toString().trim() + "/"
                    presenter.login(edtUserName.text.toString().trim(), edtPassword.text.toString().trim())
                } else {
                    UserPreferences.baseUrl = edtBaseUrl.text.toString().trim()
                    presenter.login(edtUserName.text.toString().trim(), edtPassword.text.toString().trim())
                }
            } else {
                showToast(getString(R.string.msg_empty_Field))
            }
        }

    }

    override fun goToHome() {
        NavigationManager.attachAsRoot(
            this.requireActivity() as AppCompatActivity,
            HomeFragment.newInstance(),
            tag = NavigationManager.Tags.HOME
        )
    }
}