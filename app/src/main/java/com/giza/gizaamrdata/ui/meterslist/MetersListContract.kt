package com.giza.gizaamrdata.ui.meterslist

import com.giza.gizaamrdata.data.C
import com.giza.gizaamrdata.models.Location
import com.giza.gizaamrdata.models.SearchMeterResultedObject
import com.giza.gizaamrdata.ui.base.BaseContract

/**
 * @author hossam.
 */
class MetersListContract : BaseContract {
    interface View : BaseContract.View<androidx.fragment.app.Fragment> {
        fun showQRScanner()
        fun showNumberInput()
        fun showNameInput()
        fun showGPSButtons()
        fun showResults()
        fun onUpdateQuery()
        fun updateData()
        fun onGPSRadioButtonChanged()
        fun showLoading()
        fun hideLoading()
    }

    interface Presenter: BaseContract.Presenter {
        fun startSearch(searchOptions: C.SearchOptions)
        fun loadByGPS(searchOptions: C.SearchOptions, location: Location)
        fun loadByOwnerData(query: String)
        fun loadByNumber(meterId: String)
        fun getExistedMeters() : MutableList<SearchMeterResultedObject>
        fun clearDate()
        fun destroy()
    }
}