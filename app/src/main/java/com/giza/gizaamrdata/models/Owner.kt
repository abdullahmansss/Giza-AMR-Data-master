package com.giza.gizaamrdata.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Owner(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "OwnerId")
    val id: Int = 0,
    @ColumnInfo(name = "OwnerName")
    var name: String = "",
    var phone: String = "",
    var national_id: String = "",
    var old_meter_number: String = "",
    var old_meter_reading: String = "",
    var electricity_meter: String = "",
    var custom1: String = "",
    var custom2: String = "",
    var custom3: String = "",
    var custom4: String = "",
    var custom5: String = "",
    var custom6: String = "",
    var custom7: String = "",
    var custom8: String = "",
    var custom9: String = "",
    var custom10: String = ""
    ) : Serializable