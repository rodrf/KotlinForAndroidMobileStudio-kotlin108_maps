package com.gorrotowi.kotlin108googlemaps.entitys

import com.google.gson.annotations.SerializedName

data class FieldsItem(

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("alias")
    val alias: String? = null,

    @field:SerializedName("type")
    val type: String? = null
)