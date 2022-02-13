package com.tivasoft.biconui.data.model.network.requests.doctor

import javax.annotation.Nullable

data class AddClinic(
    @Nullable val  name: String,
    @Nullable val address: String,
    @Nullable val  location : ClinicLocation,
    @Nullable val phoneNumber: String,
    @Nullable var attachment : ArrayList<String> = arrayListOf()
)

data class ClinicLocation(
    @Nullable var latitude: Double,
    @Nullable  var longitude: Double
)