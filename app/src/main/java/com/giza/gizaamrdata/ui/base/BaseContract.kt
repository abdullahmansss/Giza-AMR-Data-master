package com.giza.gizaamrdata.ui.base

/**
 * @author hossam.
 */
interface BaseContract {
    interface View<C> {
        val viewContext: C
        fun showToast(message :String)
    }

    interface Presenter
}
