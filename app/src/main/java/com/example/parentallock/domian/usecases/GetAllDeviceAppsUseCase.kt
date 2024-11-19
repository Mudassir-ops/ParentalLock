package com.example.parentallock.domian.usecases

import com.parental.control.displaytime.kids.safety.data.model.AllAppsEntity
import com.parental.control.displaytime.kids.safety.data.model.DeviceAppEntity
import com.example.parentallock.domian.repository.DeviceAppsRepository
import com.example.parentallock.utils.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class GetAllDeviceAppsUseCase @Inject constructor(private val deviceAppsRepository: DeviceAppsRepository) {

    fun invokeAllDeviceAppsUseCase(): Flow<DataState<List<AllAppsEntity>>> {
        return deviceAppsRepository.allDeviceApps()
    }

    fun invokeAllCustomLockApp(): Flow<List<DeviceAppEntity>> {
        return deviceAppsRepository.getAllCustomLockApp()
    }

    suspend fun isAppCustomLocked(givenName: String): Boolean? {
        return deviceAppsRepository.isAppCustomLocked(givenName).firstOrNull()
    }

    fun isTableEmpty(): Flow<Boolean> {
        return deviceAppsRepository.isTableEmpty()
    }
}