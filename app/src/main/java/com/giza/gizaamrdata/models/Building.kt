package com.giza.gizaamrdata.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Building(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "BuildingId")
    var id: Int = 0,
    var type: String = "",
    var usage: String = "",
    var street_type: String ="",
    var notes: String = "",
    var floors: Int = 1,
    @Embedded var address: Address?
) : Serializable
