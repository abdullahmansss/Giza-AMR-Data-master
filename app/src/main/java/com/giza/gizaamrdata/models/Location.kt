package com.giza.gizaamrdata.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * @author hossam.
 */
@Entity
data class Location(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "LocationId")
    var id: Int = 0,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var altitude: Double = 0.0,
    var accuracy: Float = 50f
) : Serializable