package com.giza.gizaamrdata.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Address(
    var place: String = "",
    var building_number: String = "",
    var city: String = "",
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "AddressId")
    var id: Int = 0,
    var neighborhood: String = "",
    var street: String = ""
) : Serializable {
    override fun toString(): String {
        var address = ""
        if (!street.isNullOrBlank()) {
            address += "$street Road"
        }
        if (!building_number.isNullOrEmpty()) {
            address += ", $building_number Building"
        }
        if (!neighborhood.isNullOrEmpty()) {
            address += ", near $neighborhood"
        }
        if (!city.isNullOrEmpty()) {
            address += ", $city city"
        }
        return address
    }
}