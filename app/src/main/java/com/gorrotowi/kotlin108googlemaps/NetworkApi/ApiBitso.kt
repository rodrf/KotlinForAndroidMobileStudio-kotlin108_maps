package com.gorrotowi.kotlin108googlemaps.NetworkApi

import com.gorrotowi.kotlin108googlemaps.BuildConfig
import com.gorrotowi.kotlin108googlemaps.NetworkApi.entityss.ResponseAvailable
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object ApiBitso {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.bitso.com/v3/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(createOkhttpClient())
        .build()

    private val bitsoEndpoints = retrofit.create<Endpoints>()


    private fun createInterceptor() = Interceptor { chain ->
        val request = chain.request().newBuilder()
        request.addHeader("Content-Type", "application/json")
        request.addHeader("Accept", "application/json")
        chain.proceed(request.build())
    }

    private fun createLoginInterceptor() = HttpLoggingInterceptor().apply {
        level = if(BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        //por seguridad par que no se muestre

    }

    private fun createOkhttpClient() = OkHttpClient.Builder().apply {
        interceptors().add(createInterceptor())
        interceptors().add(createLoginInterceptor())

    }.build()

    fun getAvailableBooks(success:(ResponseAvailable) -> Unit), Call{
        val callBooks = bitsoEndpoints.getAvailableBooks()
        callBooks.enqueue(object : Callback<ResponseAvailable> t: Throwable)
    }

}