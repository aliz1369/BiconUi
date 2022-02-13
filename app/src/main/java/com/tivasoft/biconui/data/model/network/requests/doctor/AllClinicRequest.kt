package com.tivasoft.biconui.data.model.network.requests.doctor

data class AllClinicRequest(
     val distance : Int,
     val location : LocationClinic
)

data class LocationClinic(
    val  latitude : Double,
    val longitude : Double
)
