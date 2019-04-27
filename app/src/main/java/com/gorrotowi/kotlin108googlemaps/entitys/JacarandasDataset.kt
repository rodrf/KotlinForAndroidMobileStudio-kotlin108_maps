package com.gorrotowi.kotlin108googlemaps.entitys

import com.google.gson.annotations.SerializedName

data class JacarandasDataset(

    @field:SerializedName("features")
    val jacarandas: List<FeaturesItem?>? = null,

    @field:SerializedName("displayFieldName")
    val displayFieldName: String? = null,

    @field:SerializedName("spatialReference")
    val spatialReference: SpatialReference? = null,

    @field:SerializedName("fields")
    val fields: List<FieldsItem?>? = null,

    @field:SerializedName("fieldAliases")
    val fieldAliases: FieldAliases? = null,

    @field:SerializedName("geometryType")
    val geometryType: String? = null
)