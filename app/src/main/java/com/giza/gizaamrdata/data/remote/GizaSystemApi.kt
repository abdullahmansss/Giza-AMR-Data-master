package com.giza.gizaamrdata.data.remote


import com.giza.gizaamrdata.models.*
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*


/**
 * @author hossam.
 */
interface GizaSystemApi {
    @POST("moblogin.php")
    @Headers("@: noAuth")
    fun login(@Body loginCredentials: LoginCredentials): Single<Response<com.giza.gizaamrdata.models.Response>>

    @POST("meterinsert.php")
    fun uploadMeters(@Body meters: MutableList<FlatMeter>): Single<List<MetersUploadResponse>>

    @POST("uploadimage.php")
    @Multipart
    fun uploadImages(@Part file: MultipartBody.Part, @Part("meterid") meterId: RequestBody): Single<Response<com.giza.gizaamrdata.models.Response>>

    @POST("getmeter.php")
    fun getMeters(@Body searchQuery: SearchQuery) : Single<List<SearchMeterResultedObject>>

    @POST("getmeter.php")
    fun getMeters(@Body searchQuery: SearchQueryGPS) : Single<List<SearchMeterResultedObject>>
}