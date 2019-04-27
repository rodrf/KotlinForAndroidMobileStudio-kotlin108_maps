package com.gorrotowi.kotlin108googlemaps.entitys

import com.google.gson.annotations.SerializedName

data class FeaturesItem(

    @field:SerializedName("attributes")
    val attributes: Attributes? = null,

    @field:SerializedName("geometry")
    val geometry: Geometry? = null
)