package com.example.parentallock.domian.usecases

import com.example.parentallock.data.model.DeviceAppEntity
import com.example.parentallock.domian.repository.DeviceAppsRepository
import javax.inject.Inject

class InsertDeviceAppsUseCase @Inject constructor(private val deviceAppsRepository: DeviceAppsRepository) {

    suspend fun invokeInsertCustomLockedApp(deviceAppEntity: DeviceAppEntity) {
        deviceAppsRepository.insertCustomLockedApp(deviceAppEntity)
    }
    suspend fun delete(deviceAppEntity: DeviceAppEntity) {
        return deviceAppsRepository.delete(deviceAppEntity)
    }

}