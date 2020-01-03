package com.giza.gizaamrdata.ui.authentication

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.giza.gizaamrdata.R
import com.giza.gizaamrdata.data.local.UserPreferences
import com.giza.gizaamrdata.ui.NavigationManager
import com.giza.gizaamrdata.ui.base.BaseFragment
import com.giza.gizaamrdata.ui.login.LoginFragment
import com.giza.gizaamrdata.utils.extensions.putArgs
import com.giza.gizaamrdata.utils.extensions.showToast
import kotlinx.android.synthetic.main.auth_fragment.*
import java.util.*


/**
 * @author hossam.
 */
class AuthFragment : BaseFragment<AuthContract.Presenter>(), AuthContract.View {

    override val fragmentLayoutResourceId = R.layout.auth_fragment
    private var myClipboard: ClipboardManager? = null
    private var myClip: ClipData? = null
    private lateinit var uniqueID: String

    companion object {
        fun newInstance() = AuthFragment().putArgs {
            val args = Bundle()
            val fragment = AuthFragment()
            fragment.arguments = args
            return fragment
        }

    }

    override fun init(savedInstanceState: Bundle?) {
        presenter = AuthPresenter(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        retainInstance = true
        myClipboard = this.requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onLoginClicked()
        generateInstallationCOde()
    }

    private fun generateInstallationCOde() {
        uniqueID = UUID.randomUUID().toString()
        val decodedBytes = Base64.encodeToString(uniqueID.toByteArray(), Base64.DEFAULT)
        UserPreferences.uuid = decodedBytes.toString()
        var prefix = getString(R.string.codePrefix)
        val spannableText = SpannableStringBuilder("$prefix :\n$uniqueID")

        val foreground = ForegroundColorSpan(getResColor(R.color.code_span_selector))
        val textStyle = StyleSpan(Typeface.BOLD)

        spannableText.setSpan(
            foreground,
            spannableText.indexOf("\n"),
            spannableText.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        spannableText.setSpan(
            textStyle,
            spannableText.indexOf("\n"),
            spannableText.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        txtCode.setText(spannableText, TextView.BufferType.SPANNABLE)
    }

    override fun onLoginClicked() {
        btnLogin.setOnClickListener {
            if (edtPassword.text.toString().trim() == UserPreferences.uuid?.trim()) {
                UserPreferences.authorized = true
                presenter.navigateToLogin()
            } else {
                it.context.showToast(getString(R.string.msg_wrong_password))
            }
        }
        btnPaste.setOnClickListener {
            pasteText()
        }
        txtCode.setOnClickListener {
            copyText()
        }
    }

    override fun goToLogin() {
        NavigationManager.attachAsRoot(
            this.requireActivity() as AppCompatActivity,
            LoginFragment.newInstance(),
            tag = NavigationManager.Tags.LOGIN
        )
    }

    // on click copy button
    fun copyText() {
        myClip = ClipData.newPlainText("text", uniqueID)
        myClipboard?.setPrimaryClip(myClip!!)
        this.requireContext().showToast(getString(R.string.copied))
    }

    // on click paste button
    fun pasteText() {
        val abc = myClipboard?.primaryClip
        val item = abc?.getItemAt(0)
        edtPassword.setText(item?.text.toString(), TextView.BufferType.EDITABLE)
        this.requireContext().showToast(getString(R.string.pasted))

    }
}