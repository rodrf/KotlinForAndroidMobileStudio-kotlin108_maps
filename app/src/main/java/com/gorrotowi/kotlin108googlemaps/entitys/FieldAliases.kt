package com.gorrotowi.kotlin108googlemaps.entitys

import com.google.gson.annotations.SerializedName

data class FieldAliases(

    @field:SerializedName("FID")
    val fID: String? = null,

    @field:SerializedName("POINT_Y")
    val pOINTY: String? = null,

    @field:SerializedName("POINT_X")
    val pOINTX: String? = null,

    @field:SerializedName("Id")
    val id: String? = null
)