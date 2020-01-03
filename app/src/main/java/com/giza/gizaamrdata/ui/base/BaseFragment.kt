package com.giza.gizaamrdata.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatDelegate
import com.giza.gizaamrdata.utils.extensions.showToast

/**
 * @author hossam.
 */

abstract class BaseFragment<P : BaseContract.Presenter> : androidx.fragment.app.Fragment(),
    BaseContract.View<androidx.fragment.app.Fragment> {

    lateinit var presenter: P
    private lateinit var rootView: View

    @get:LayoutRes
    protected abstract val fragmentLayoutResourceId: Int

    override val viewContext: androidx.fragment.app.Fragment
        get() = this

    protected abstract fun init(savedInstanceState: Bundle?)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (!::rootView.isInitialized) {
            rootView = inflater.inflate(fragmentLayoutResourceId, container, false)
        }
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        init(savedInstanceState)
        return rootView
    }

    fun getResColor(id: Int): Int {
        return resources.getColor(id)
    }

    override fun showToast(message: String) {
       viewContext.context?.showToast(message)
    }

    override fun onResume() {
        com.giza.gizaamrdata.utils.Logger.d("===========  Fragment ${this.javaClass.simpleName} is resumed  ==============")
        super.onResume()
    }

    override fun onStop() {
        com.giza.gizaamrdata.utils.Logger.d("  ===========  Fragment ${this.javaClass.simpleName} is stopped  ==============")
        super.onStop()
    }
}
