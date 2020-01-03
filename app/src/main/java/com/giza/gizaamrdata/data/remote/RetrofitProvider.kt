package com.giza.gizaamrdata.data.remote

import android.content.Intent
import com.giza.gizaamrdata.GizaApp
import com.giza.gizaamrdata.data.local.UserPreferences
import com.giza.gizaamrdata.models.LoginCredentials
import com.giza.gizaamrdata.models.Meter
import com.giza.gizaamrdata.models.SearchQuery
import com.giza.gizaamrdata.models.SearchQueryGPS
import com.giza.gizaamrdata.ui.MainActivity
import com.giza.gizaamrdata.utils.Logger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext


/**a
 * @author hossam.
 */
object RetrofitProvider {

    var BASE_URL = UserPreferences.baseUrl ?: "http://www.purediagnosticseg.com/amrconfig/"

    private lateinit var gizaSystemApi: GizaSystemApi
    private var retrofit = initRetrofit(BASE_URL)
    private lateinit var client: OkHttpClient

    init {
        initServices()
    }

    private fun initRetrofit(baseUrl: String): Retrofit {

        //fixs Javax.net.ssl.SSLHandshakeException: javax.net.ssl.SSLProtocolException:
        // SSL handshake aborted: Failure in SSL library, usually a protocol error
        val sslContext = SSLContext.getInstance("TLSv1.2")
        sslContext.init(null, null, null)
        val engine = sslContext.createSSLEngine()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(provideOkHttpClient())
            .build()
    }


    private fun initServices() {
        gizaSystemApi = retrofit.create(GizaSystemApi::class.java)
    }

    private fun provideOkHttpClient(): OkHttpClient {
        client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor { chain ->
                val original = chain.request()
                Logger.d("interceptor  ")

                var requestBuilder = original.newBuilder();
                if (chain.request().headers.values("@").size != 1) {
                    requestBuilder = original.newBuilder().header("amrtoken", UserPreferences.header ?: "")
                } else {
                    requestBuilder.removeHeader("amrtoken")
                }
                // Request customization: add request headers
                requestBuilder.removeHeader("@")
                requestBuilder.header("user-agent", "android")
                val request = requestBuilder.build()
                chain.proceed(request)
            }.addInterceptor { chain ->
                val original = chain.request()

                val request = original.newBuilder()
                    .method(original.method, original.body)
                    .build()


                val response = chain.proceed(request)
                Logger.d("Response code : ${response.code}")
                when {
                    response.code == 200 -> response
                    response.code == 401 -> {
                        val intent = Intent(GizaApp.instance.applicationContext, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        GizaApp.instance.applicationContext.startActivity(intent)
                        throw Exception("Error 401 ,${response.body.toString()} \nrestarting the app...")
                    }
                    else -> throw Exception("Unknown Error ${response.body.toString()}")
                }
            }.build()

        return client
    }

    fun login(userName: String, password: String) =
        gizaSystemApi.login(LoginCredentials(userName, password))
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())

    fun uploadMeters(meters: MutableList<Meter>) =
        gizaSystemApi.uploadMeters(Meter.convertToFlatMeters(meters))
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())

    fun uploadImages(file: MultipartBody.Part, meterId: RequestBody) =
        gizaSystemApi.uploadImages(file, meterId)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())

    fun getMeterById(meterId: String) =
        gizaSystemApi.getMeters(SearchQuery(Field = "Meter", Criteria = meterId))
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())

    fun getMeterByOwnerData(query: String) = gizaSystemApi.getMeters(SearchQuery(Field = "Customer", Criteria = query))
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())

    fun getMeterByGPS(latLongDistance: ArrayList<String>) =
        gizaSystemApi.getMeters(SearchQueryGPS(Field = "LONLAT", Criteria = latLongDistance))
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())

    fun updateRetrofit(baseUrl: String) {
        BASE_URL = baseUrl
        retrofit = initRetrofit(BASE_URL)
        initServices()
        Logger.d("updateRetrofit called and Retrofit Provider now is using $BASE_URL")
    }

}