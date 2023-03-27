package com.ellingsengroup.products.network

import androidx.lifecycle.MutableLiveData
import com.ellingsengroup.products.BuildConfig
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


data class Error(val code: Int, val msg: String)

object RequestBuilder {

    private val loggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        this.level = HttpLoggingInterceptor.Level.BODY
    }
    private val onServerError by lazy {
        MutableLiveData<Error>()
    }

    private val client =
        OkHttpClient().newBuilder().apply {
            this.addInterceptor(loggingInterceptor)
            this.connectTimeout(30, TimeUnit.SECONDS)
            this.readTimeout(30, TimeUnit.SECONDS)
            this.addInterceptor { chain: Interceptor.Chain ->

                val requestBuilder: Request.Builder = chain.request().newBuilder()
                chain.proceed(requestBuilder.build())
            }
            this.addInterceptor { chain: Interceptor.Chain ->
                val request = chain.request()

                val response = chain.proceed(request)

                if (response.code == 400 ||
                    response.code == 401 ||
                    response.code == 403 ||
                    response.code == 500 ||
                    response.code == 502 ||
                    response.code == 503
                ) {
                    onServerError.postValue(Error(response.code, ""))
                }
                response
            }
        }.build()



    private fun retrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    fun createJsonRequestBodyAny(map: Map<String, Any?>) =
        JSONObject(map).toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

    val apiRequest: EndPoints = retrofit().create(EndPoints::class.java)

}