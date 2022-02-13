package com.tivasoft.biconui.utils.channel_intents

import com.tivasoft.biconui.data.model.network.requests.doctor.CreatePackageRequest

/**
 * Generic class for telling viewModel what should be done and with what parameters.
 */
sealed class PackageIntent {
    object GetAdminPackages : PackageIntent()
    object GetPatientPackages : PackageIntent()
    object GetDoctorPackages : PackageIntent()
    object GetPeriods : PackageIntent()
    object GetItems : PackageIntent()
    class CreatePackage(val entity: CreatePackageRequest) : PackageIntent()
    class ChangePackageActivation(val packageId: String) : PackageIntent()
    class GetDoctorPackagesById(val doctorId: String) : PackageIntent()
}