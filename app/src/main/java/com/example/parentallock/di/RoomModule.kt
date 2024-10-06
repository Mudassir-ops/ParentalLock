package com.example.parentallock.di

import android.content.Context
import androidx.room.Room
import com.example.parentallock.data.ParentalLockDatabase
import com.example.parentallock.data.dao.DeviceAppsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {
    @Provides
    @Singleton
    fun provideParentalAppLockDatabase(@ApplicationContext context: Context): ParentalLockDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            ParentalLockDatabase::class.java,
            "parental_app_lock_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideDeviceAppsDao(database: ParentalLockDatabase): DeviceAppsDao {
        return database.updatedAppDao()
    }

}