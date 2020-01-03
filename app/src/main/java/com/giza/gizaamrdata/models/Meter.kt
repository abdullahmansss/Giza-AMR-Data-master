package com.giza.gizaamrdata.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.giza.gizaamrdata.utils.DataUtils
import java.io.Serializable

/**
 * @author hossam.
 */
@Entity(tableName = "Meters")
data class Meter(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var number: String = "",
    val created_at: String = System.currentTimeMillis().toString(),
    var vendor: String = "",
    var state: String = "",
    @Embedded var building: Building = Building(address = Address()),
    var urls: MutableList<String> = mutableListOf(),
    var actions: MutableList<String> = mutableListOf(),
    @Embedded var owner: Owner = Owner(),
    @Embedded var location: Location = Location()
) : Serializable {

    fun actionsToString(): String {
        return if (actions.isNullOrEmpty()) {
            ""
        } else {
            actions.joinToString("|", "[", "]")
        }
    }

    fun toRow(): Array<String> {
        return arrayOf(
            id.toString(),
            DataUtils.getDateFromMillies(created_at),
            actionsToString(),
            number,
            building.address?.place ?: "",
            vendor,
            state,
            building.type,
            building.usage,
            building.street_type,
            building.notes,
            owner.name,
            owner.phone,
            owner.national_id,
            owner.old_meter_number,
            owner.old_meter_reading,
            owner.electricity_meter,
            owner.custom1,
            owner.custom2,
            owner.custom3,
            owner.custom4,
            owner.custom5,
            owner.custom6,
            owner.custom7,
            owner.custom8,
            owner.custom9,
            owner.custom10,
            location.latitude.toString(),
            location.longitude.toString(),
            location.accuracy.toString()
        )
    }


    companion object {
        @JvmStatic
        fun getHeadersNames(): Array<String> {
            return arrayOf(
                "ID",
                "Created at",
                "Actions",
                //page1
                "Number",
                "Address",
                //page2
                "Vendor",
                "State",
                "Building Type",
                "Building Usage",
                "Street Type",
                "Notes",
                //page3
                "Owner Name",
                "Owner Phone",
                "Owner National_id",
                "Old Meter Number",
                "Old Meter Readings",
                "Electricity Meter Number",
                "Custom1",
                "Custom2",
                "Custom3",
                "Custom4",
                "Custom5",
                "Custom6",
                "Custom7",
                "Custom8",
                "Custom9",
                "Custom10",
                //page4
                "Location Latitude",
                "Location Longitude",
                "Location Accuracy"
            )
        }

        @JvmStatic
        fun convertToFlatMeters(meters: MutableList<Meter>): MutableList<FlatMeter> {
            val flatMeters = mutableListOf<FlatMeter>()
            for (m in meters) {
                val flatMeter = FlatMeter()
                flatMeter.Address = m.building.address?.place.toString()
                flatMeter.Building_Type = m.building.type
                flatMeter.Building_Usage = m.building.usage
                flatMeter.Custom1 = m.owner.custom1
                flatMeter.Custom2 = m.owner.custom2
                flatMeter.Custom3 = m.owner.custom3
                flatMeter.Custom4 = m.owner.custom4
                flatMeter.Custom5 = m.owner.custom5
                flatMeter.Custom6 = m.owner.custom6
                flatMeter.Custom7 = m.owner.custom7
                flatMeter.Custom8 = m.owner.custom8
                flatMeter.Custom9 = m.owner.custom9
                flatMeter.Custom10 = m.owner.custom10
                flatMeter.Electricity_Meter_Number = m.number
                flatMeter.Location_Accuracy = m.location.accuracy.toString()
                flatMeter.Location_Latitude = m.location.latitude.toString()
                flatMeter.Location_Longitude = m.location.longitude.toString()
                flatMeter.Notes = m.building.notes
                flatMeter.Number = m.number
                flatMeter.Old_Meter_Number = m.owner.old_meter_number
                flatMeter.Old_Meter_Readings = m.owner.old_meter_reading
                flatMeter.Owner_Name = m.owner.name
                flatMeter.Owner_National_id = m.owner.national_id
                flatMeter.Owner_Phone = m.owner.phone
                flatMeter.State = m.state
                flatMeter.Street_Type = m.building.street_type
                flatMeter.Vendor = m.vendor
                flatMeter.Area = m.building.address?.place.toString()
                flatMeters.add(flatMeter)
            }
            return flatMeters
        }

        @JvmStatic
        fun convertToMeter(searchM: SearchMeterResultedObject): Meter {
            val meter = Meter()
            meter.building.address?.place = searchM.Address
            meter.building.type = searchM.Building_Type ?: ""
            meter.building.usage = searchM.Building_Usage
            meter.owner.custom1 = searchM.Custom1
            meter.owner.custom2 = searchM.Custom2
            meter.owner.custom3 = searchM.Custom3
            meter.owner.custom4 = searchM.Custom4
            meter.owner.custom5 = searchM.Custom5
            meter.owner.custom6 = searchM.Custom6
            meter.owner.custom7 = searchM.Custom7
            meter.owner.custom8 = searchM.Custom8
            meter.owner.custom9 = searchM.Custom9
            meter.owner.custom10 = searchM.Custom10
            meter.number = searchM.Electricity_Meter_Number
            if (searchM.Location_Accuracy.isNotEmpty()) {
                meter.location.accuracy = searchM.Location_Accuracy.toFloat()
            } else {
                meter.location.accuracy = 10f
            }
            meter.location.latitude = searchM.Location_Latitude.toDouble()
            meter.location.longitude = searchM.Location_Longitude.toDouble()
            meter.building.notes = searchM.Notes
            meter.number = searchM.Number
            meter.owner.old_meter_number = searchM.Old_Meter_Number
            meter.owner.old_meter_reading = searchM.Old_Meter_Readings
            meter.owner.name = searchM.Owner_Name
            meter.owner.national_id = searchM.Owner_National_id
            meter.owner.phone = searchM.Owner_Phone
            meter.state = searchM.State
            meter.building.street_type = searchM.Street_Type
            meter.vendor = searchM.Vendor
            meter.building.address?.place = searchM.Area
            meter.urls = searchM.Images.toMutableList()
            return meter
        }
    }
}