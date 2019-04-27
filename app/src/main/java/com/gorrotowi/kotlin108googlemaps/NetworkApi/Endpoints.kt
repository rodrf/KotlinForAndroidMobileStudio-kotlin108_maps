package com.gorrotowi.kotlin108googlemaps.NetworkApi

import com.gorrotowi.kotlin108googlemaps.NetworkApi.entityss.ResponseAvailable
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET


interface Endpoints {
    @GET("available_boos/")
    fun getAvailableBooks(): Call<ResponseAvailable>
}