package com.giza.gizaamrdata.ui.home

import com.giza.gizaamrdata.data.C
import com.giza.gizaamrdata.models.Meter
import com.giza.gizaamrdata.ui.base.BaseContract

/**
 * @author hossam.
 */
class HomeContract : BaseContract {
    interface View : BaseContract.View<androidx.fragment.app.Fragment> {
        fun onAddNewClicked()
        fun goToRegister()
        fun goToSearch(searchOptions: C.SearchOptions)
        fun onSync()
        fun showSearchDialog()
        fun hideSearchIcon()
        fun showSearchIcon()
        fun navigateToSettings()
    }

    interface Presenter : BaseContract.Presenter {
        fun navigateToAddNew()
        fun uploadMeters()
        fun uploadImages(meters: MutableList<Meter>)
    }
}