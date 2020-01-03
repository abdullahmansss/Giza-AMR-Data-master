package com.giza.gizaamrdata.ui.meterslist

import com.giza.gizaamrdata.models.SearchMeterResultedObject


/**
 * @author hossam.
 */
object MetersModel {
    var meters: MutableList<SearchMeterResultedObject> = mutableListOf()
    var page = 0

    fun destroy() {
        page = 0
        meters.clear()
    }
}