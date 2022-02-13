package com.tivasoft.biconui.utils

import com.amap.api.services.district.DistrictItem
import com.tivasoft.biconui.data.model.local.ui.Message
import com.tivasoft.biconui.data.model.network.requests.doctor.ItemPackageData
import com.tivasoft.biconui.data.model.network.responses.doctor.AllPatientData
import com.tivasoft.biconui.data.model.network.responses.doctor.AssistanceData
import com.tivasoft.biconui.data.model.network.responses.assistant.DoctorByAssistanceData
import com.tivasoft.biconui.data.model.network.responses.doctor.SpecialtiesData
import com.tivasoft.biconui.data.model.network.responses.auth.SearchData

fun interface TestItemCLickListener {
    fun onItemClickListener()
}

interface DoctorItemListener {
    fun onDoctorAction(isSelected: Boolean, doctor: SearchData)
}

interface SpecialtyItemListener {
    fun onSpecialtyAction(specialty: SpecialtiesData)
}

interface SearchedItemListener {
    fun onBookAction(item: SearchData)
    fun onPackageClicked(doctorId: String)
}

interface BookAssistanceItemListener {
    fun onBookAction(item: AssistanceData)
}

interface BookDoctorItemListener {
    fun onBookAction(item: DoctorByAssistanceData)
}

fun interface PackageItemListener {
    fun onActivationClickListener(packageId: String)
}

interface ItemPackageRemoveListener {
    fun onRemoveAction(item: ItemPackageData)
}

interface DistrictItemListener {
    fun onDistrictsAction(district: DistrictItem)
}

interface ConversationActionsListener {
    fun onConfirmAction(patientId: String)
    fun onRejectAction(patientId: String)
    fun onItemClickListener(conversationId: String, name: String)
}

interface PatientItemListener {
    fun onPatientAction(patient: AllPatientData)
}


fun interface ListItemClickListener {
    fun onItemClickListener(index: Int)
}

interface ChatItemClickListener {
    fun onItemClickListener(message: Message)
}

fun interface DoctorScheduleRefreshListener {
    fun onScheduleAdded()
}

fun interface PrescriptionListener {
    fun onPrescriptionItemClicked(type: Int, id: String)
}

fun interface OnItemClickListener {
    fun onItemClicked(uri: String)
}

fun interface OnAttachmentRemovedListener {
    fun onItemRemoved(index: Int)
}

interface AssistanceListener {
    fun onAccepted(id: String)
    fun onRejected(id: String)
    fun onClicked(assistance: AssistanceData)
}