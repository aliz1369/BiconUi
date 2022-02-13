package com.tivasoft.biconui.data.model.network.responses.doctor

data class SpecialtiesResponse(
    val success: Boolean,
    val statusCodeval: Int,
    val data: ArrayList<SpecialtiesData>
)


data class SpecialtiesData(
    val id: String,
    val name: String,
    var isChecked: Boolean = false
) {
    override fun toString(): String {
        return name
    }
}
