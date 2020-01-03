package com.giza.gizaamrdata.models

import java.io.Serializable

data class SearchMeterResultedObject(
    val Address: String,
    val Area: String,
    val Building_Type: String,
    val Building_Usage: String,
    val Custom1: String,
    val Custom2: String,
    val Custom3: String,
    val Custom4: String,
    val Custom5: String,
    val Custom6: String,
    val Custom7: String,
    val Custom8: String,
    val Custom9: String,
    val Custom10: String,
    val Electricity_Meter_Number: String,
    val Images: List<String>,
    val Location_Accuracy: String,
    val Location_Latitude: String,
    val Location_Longitude: String,
    val Notes: String,
    val Number: String,
    val Old_Meter_Number: String,
    val Old_Meter_Readings: String,
    val Owner_Name: String,
    val Owner_National_id: String,
    val Owner_Phone: String,
    val State: String,
    val Street_Type: String,
    val Vendor: String
) : Serializable