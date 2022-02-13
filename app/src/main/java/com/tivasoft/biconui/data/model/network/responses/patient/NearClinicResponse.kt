package com.tivasoft.biconui.data.model.network.responses.patient

data class NearClinicResponse(
     val success : Boolean,
     val statusCode : Int,
     val  data : ArrayList<NearClinicData>
)

data class NearClinicData(
    val id: String,
    val name : String,
    val address: String,
    val location : NearClinicLocation,
    val phoneNumber : String,
    val attachment : ArrayList<String>,
    val doctor : DoctorClinic
)


data class DoctorClinic(
   val educationDegree : String,
   val educationField : String
)

data class NearClinicLocation(
    val latitude : Double,
    val longitude : Double
)


