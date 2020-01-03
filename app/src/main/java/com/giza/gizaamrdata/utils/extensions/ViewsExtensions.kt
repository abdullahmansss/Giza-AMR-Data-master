package com.giza.gizaamrdata.utils.extensions

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout


/**
 * @author hossam.
 */
fun View.show() {
    if (isVisible()) return
    this.visibility = View.VISIBLE
}

fun View.hide() {
    if (!isVisible()) return
    this.visibility = View.GONE
}

fun View.softHide() {
    if (!isVisible()) return
    this.visibility = View.INVISIBLE
}

fun View.isVisible(): Boolean {
    return this.visibility == View.VISIBLE
}

fun SwipeRefreshLayout.showLoadingView() {
    isRefreshing = true
}

fun SwipeRefreshLayout.hideLoadingView() {
    isRefreshing = false
}

fun Button.activate() {
    this.isEnabled = true
    this.alpha = 1.0f
}

fun Button.deActivate() {
    this.isEnabled = false
    this.alpha = 0.5f
}

fun View.showLoading() {
    this.softHide()
    val l = IntArray(2)
    this.getLocationOnScreen(l)
    val w = this.width
    var h = this.height
    val loadingView: View = ProgressBar(this.context)

    val p = RelativeLayout.LayoutParams(w, h).apply {
        addRule(RelativeLayout.ABOVE, this@showLoading.id)
        addRule(RelativeLayout.ALIGN_BOTTOM, this@showLoading.id)
        addRule(RelativeLayout.ALIGN_LEFT, this@showLoading.id)
    }

    if (this.parent != null) (this.parent as ViewGroup).addView(loadingView, p)
}

fun View.hideLoading(withDrawble: Boolean = false) {
    (this.parent as ViewGroup).removeViewAt((this.parent as ViewGroup).childCount - 1)
    if (withDrawble)
        (this.parent as ViewGroup).removeViewAt((this.parent as ViewGroup).childCount - 1)
    this.show()
}

fun EditText.onChange(cb: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            cb(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}