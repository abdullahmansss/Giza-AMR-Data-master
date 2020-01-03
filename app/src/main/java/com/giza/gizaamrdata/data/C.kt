package com.giza.gizaamrdata.data

/**
 * @author hossam.
 */
object C {

    object Database {
        const val NAME = "MetersDatabase.db"
        const val IMAGE_DIR = "GizaMeters"
    }

    object RequestsCodes {
        const val PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1
        const val REQUEST_CHECK_SETTINGS = 2
    }

    enum class SearchOptions {
        QR,NUMBER,NAME,NATIONAL_ID,GPS10,GPS20,GPS50
    }
}