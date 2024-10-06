package com.example.parentallock.domian.repository

import com.example.parentallock.data.model.AllAppsEntity
import com.example.parentallock.data.model.DeviceAppEntity
import com.example.parentallock.utils.DataState
import kotlinx.coroutines.flow.Flow

typealias AllDeviceAppsFunction = () -> Flow<DataState<List<AllAppsEntity>>>

interface DeviceAppsRepository {

    val allDeviceApps: AllDeviceAppsFunction
    suspend fun insertCustomLockedApp(deviceAppEntity: DeviceAppEntity)
    fun getAllCustomLockApp(): Flow<List<DeviceAppEntity>>
    fun isAppCustomLocked(givenName: String): Flow<Boolean>
    fun isTableEmpty(): Flow<Boolean>
    suspend fun delete(deviceAppEntity: DeviceAppEntity)
}