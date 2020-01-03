package com.giza.gizaamrdata.utils

import androidx.room.FtsOptions

/**
 * @author hossam.
 */
class RxEvents {

    enum class Wizard {
        PAGE1,
        PAGE2,
        PAGE3,
        PAGE4,
        PAGE5,
        CHECK_NEXT,
        ACTIVATE_NEXT,
        DE_ACTIVATE_NEXT
    }

    enum class HomeUtils {
        BackHome
    }
    data class AddAcceptedOrder(var order: FtsOptions.Order)
}