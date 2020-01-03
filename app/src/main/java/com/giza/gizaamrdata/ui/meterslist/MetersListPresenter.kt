package com.giza.gizaamrdata.ui.meterslist

import android.annotation.SuppressLint
import com.giza.gizaamrdata.data.C
import com.giza.gizaamrdata.data.remote.RetrofitProvider
import com.giza.gizaamrdata.models.Location
import com.giza.gizaamrdata.models.SearchMeterResultedObject
import com.giza.gizaamrdata.ui.base.BasePresenter
import com.giza.gizaamrdata.utils.Logger

/**
 * @author hossam.
 */
class MetersListPresenter(view: MetersListContract.View) : BasePresenter<MetersListContract.View>(view),
    MetersListContract.Presenter {
    @SuppressLint("CheckResult")
    override fun loadByGPS(searchOptions: C.SearchOptions, location: Location) {
        clearDate()
        view.get()?.showLoading()
        val distance = when (searchOptions) {
            C.SearchOptions.GPS10 -> 10
            C.SearchOptions.GPS20 -> 20
            C.SearchOptions.GPS50 -> 50
            else -> 50
        }
        RetrofitProvider.getMeterByGPS(
            arrayListOf(
                location.latitude.toString(),
                location.longitude.toString(),
                distance.toString()
            )
        ).subscribe({
            //            Logger.e("Login request success with response code ${it.code()} body ${it.body()?.status} ${it.body()?.status_message}")
            Logger.e("loadByGPS request success")
            MetersModel.meters = it.toMutableList()
            view.get()?.updateData()
            view.get()?.hideLoading()
        }, {
            Logger.e("loadByNumber request has been failed code due to ${it.message}")
            view.get()?.hideLoading()
        })
    }

    @SuppressLint("CheckResult")
    override fun loadByOwnerData(query: String) {
        clearDate()
        view.get()?.showLoading()
        RetrofitProvider.getMeterByOwnerData(query).subscribe({
            //            Logger.e("Login request success with response code ${it.code()} body ${it.body()?.status} ${it.body()?.status_message}")
            Logger.e("loadByNumber request success")
            MetersModel.meters = it.toMutableList()
            view.get()?.updateData()
            view.get()?.hideLoading()
        }, {
            Logger.e("loadByNumber request has been failed code due to ${it.message}")
            view.get()?.hideLoading()
        })
    }

    @SuppressLint("CheckResult")
    override fun loadByNumber(meterId: String) {
        clearDate()
        view.get()?.showLoading()
        RetrofitProvider.getMeterById(meterId).subscribe({
            //            Logger.e("Login request success with response code ${it.code()} body ${it.body()?.status} ${it.body()?.status_message}")
            Logger.e("loadByNumber request success")
            MetersModel.meters = it.toMutableList()
            view.get()?.updateData()
            view.get()?.hideLoading()
        }, {
            Logger.e("loadByNumber request has been failed code due to ${it.message}")
            view.get()?.hideLoading()
        })
    }

    override fun startSearch(searchOptions: C.SearchOptions) {
        when (searchOptions) {
            C.SearchOptions.GPS10, C.SearchOptions.GPS20, C.SearchOptions.GPS50 -> view.get()?.showGPSButtons()
            C.SearchOptions.NAME -> view.get()?.showNameInput()
            C.SearchOptions.NATIONAL_ID -> view.get()?.showNameInput()
            C.SearchOptions.NUMBER -> view.get()?.showNumberInput()
            C.SearchOptions.QR -> view.get()?.showQRScanner()
        }
        view.get()?.hideLoading()
    }

    override fun getExistedMeters(): MutableList<SearchMeterResultedObject> {
        return MetersModel.meters
    }

    override fun clearDate() {
        MetersModel.destroy()
        view.get()?.updateData()
    }

    override fun destroy() {
        MetersModel.destroy()
    }
}