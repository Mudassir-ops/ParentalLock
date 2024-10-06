package com.example.parentallock.data.data

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.example.parentallock.data.dao.DeviceAppsDao
import com.example.parentallock.data.model.AllAppsEntity
import com.example.parentallock.data.model.DeviceAppEntity
import com.example.parentallock.domian.repository.AllDeviceAppsFunction
import com.example.parentallock.domian.repository.DeviceAppsRepository
import com.example.parentallock.utils.DataState
import getPackageInfoCompat
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class DeviceAppsRepositoryImpl @Inject constructor(
    private val applicationContext: Context,
    private val deviceAppsDao: DeviceAppsDao
) : DeviceAppsRepository {

    override val allDeviceApps: AllDeviceAppsFunction = {
        flow {
            val allSysApps: List<DeviceAppEntity?> =
                installedApps(context = applicationContext)
            val allSysAppsInner = mutableListOf<AllAppsEntity>()
            allSysApps.forEach { packageInfoEntity ->
                allSysAppsInner.add(
                    AllAppsEntity(
                        appName = packageInfoEntity?.appName ?: "",
                        pName = packageInfoEntity?.pName ?: "",
                        versionName = packageInfoEntity?.versionName ?: "",
                        versionCode = packageInfoEntity?.versionCode ?: 0,
                        icon = packageInfoEntity?.icon,
                        installationDate = packageInfoEntity?.installationDate ?: "",
                    )
                )
            }
            emit(DataState.Success(allSysAppsInner))
        }
    }

    override suspend fun insertCustomLockedApp(deviceAppEntity: DeviceAppEntity) {
        deviceAppsDao.insertCustomLockedApp(deviceAppEntity)
    }

    override fun getAllCustomLockApp(): Flow<List<DeviceAppEntity>> {
        return deviceAppsDao.getAllCustomLockApp()
    }

    override fun isAppCustomLocked(givenName: String): Flow<Boolean> {
        return deviceAppsDao.isAppCustomLocked(givenName)
    }

    override fun isTableEmpty(): Flow<Boolean> {
        return deviceAppsDao.isTableEmpty()
    }

    override suspend fun delete(deviceAppEntity: DeviceAppEntity) {
        return deviceAppsDao.delete(deviceAppEntity)
    }

    private suspend fun installedApps(
        context: Context?,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): List<DeviceAppEntity> = withContext(dispatcher) {
        val res: ArrayList<DeviceAppEntity> = ArrayList()
        val packs: List<PackageInfo?> =
            context?.packageManager?.getInstalledPackages(0) ?: emptyList()
        for (pack in packs) {
            if (pack != null && (pack.applicationInfo?.flags?.and(ApplicationInfo.FLAG_SYSTEM)) == 0) {
                val newInfo = packageInfoEntity(context, pack)
                if (newInfo.icon != null) {
                    res.add(newInfo)
                }
            }
        }
        return@withContext res
    }

    private fun packageInfoEntity(context: Context?, pack: PackageInfo?): DeviceAppEntity {
        return DeviceAppEntity(
            appName = context?.packageManager?.let {
                pack?.applicationInfo?.loadLabel(it)?.toString()
            } ?: "",
            pName = pack?.packageName ?: "",
            versionName = pack?.versionName ?: "",
            versionCode = 0,
            icon = pack?.applicationInfo?.loadIcon(context?.packageManager),
            installationDate = installationDate(context, pack?.packageName ?: "")
        )
    }

    private fun installationDate(context: Context?, packageName: String): String {
        try {
            val packageInfo = context?.packageManager?.getPackageInfoCompat(packageName)
            val installationTime = packageInfo?.lastUpdateTime ?: 0
            val dateFormat = java.text.SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            return dateFormat.format(Date(installationTime))
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return ""
    }

}