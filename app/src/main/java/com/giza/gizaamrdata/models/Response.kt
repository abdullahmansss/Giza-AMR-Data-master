package com.giza.gizaamrdata.models

data class Response(
    val status: Int,
    val status_message: String,
    val api_url: String?
)