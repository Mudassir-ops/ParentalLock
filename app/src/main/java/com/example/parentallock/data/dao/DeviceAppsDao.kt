package com.example.parentallock.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.parentallock.data.model.DeviceAppEntity

@Dao
interface DeviceAppsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomLockedApp(deviceAppEntity: DeviceAppEntity)

    @Query("SELECT * FROM package_info_table")
    fun getAllCustomLockApp(): kotlinx.coroutines.flow.Flow<List<DeviceAppEntity>>


    @Query("SELECT COUNT(*) > 0 FROM package_info_table WHERE pName = :givenName")
    fun isAppCustomLocked(givenName: String): kotlinx.coroutines.flow.Flow<Boolean>

    @Query("SELECT (COUNT(*) = 0) FROM package_info_table")
    fun isTableEmpty(): kotlinx.coroutines.flow.Flow<Boolean>

    @Delete
    suspend fun delete(deviceAppEntity: DeviceAppEntity)

}