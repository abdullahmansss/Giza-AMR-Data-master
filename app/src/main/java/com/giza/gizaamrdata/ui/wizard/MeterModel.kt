package com.giza.gizaamrdata.ui.wizard

import com.giza.gizaamrdata.models.Meter

/**
 * @author hossam.
 */
object MeterModel {
    var pageId = 0
    var meter =  Meter()
    var completed = true
    var isImagesUpdated = false
    fun destroy() {
        completed = true
        meter =  Meter()
        pageId = 0
        isImagesUpdated = false
    }
}