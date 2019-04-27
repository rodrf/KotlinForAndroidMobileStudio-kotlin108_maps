package com.gorrotowi.kotlin108googlemaps.entitys

import com.google.gson.annotations.SerializedName

data class Attributes(

    @field:SerializedName("FID")
    val fID: Int? = null,

    @field:SerializedName("POINT_Y")
    val pOINTY: Double? = null,

    @field:SerializedName("POINT_X")
    val pOINTX: Double? = null,

    @field:SerializedName("Id")
    val id: Int? = null
)