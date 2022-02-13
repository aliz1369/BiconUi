package com.tivasoft.biconui.data.model.network.requests.patient

import javax.annotation.Nullable

data class FilterModel(
    @Nullable val genderType:String,
    @Nullable val specialties:String,
    @Nullable val consulting:String,
    @Nullable val clas:String,
    @Nullable val search:String
)
