package com.gorrotowi.kotlin108googlemaps.entitys

import com.google.gson.annotations.SerializedName

data class Geometry(

    @field:SerializedName("x")
    val X: Double? = null,

    @field:SerializedName("y")
    val Y: Double? = null
)