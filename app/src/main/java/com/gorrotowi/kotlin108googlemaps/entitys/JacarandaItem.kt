package com.gorrotowi.kotlin108googlemaps.entitys

import com.google.android.gms.maps.model.LatLng

data class JacarandaItem(
    val id: Int,
    val location: LatLng,
    val address: String? = null
)