package com.example.parentallock.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.parentallock.data.dao.DeviceAppsDao
import com.example.parentallock.data.model.DeviceAppEntity

@Database(entities = [DeviceAppEntity::class], version = 1, exportSchema = false)
abstract class ParentalLockDatabase : RoomDatabase() {
    abstract fun updatedAppDao(): DeviceAppsDao
}