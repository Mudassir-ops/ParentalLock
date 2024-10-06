package com.example.parentallock.di

import android.content.Context
import android.content.SharedPreferences
import com.example.parentallock.data.dao.DeviceAppsDao
import com.example.parentallock.data.data.DeviceAppsRepositoryImpl
import com.example.parentallock.domian.repository.DeviceAppsRepository
import com.example.parentallock.utils.AppConstants
import com.example.parentallock.utils.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAllDeviceAppsRepository(
        @ApplicationContext context: Context,
        deviceAppsDao: DeviceAppsDao
    ): DeviceAppsRepository {
        return DeviceAppsRepositoryImpl(context, deviceAppsDao)
    }

    @Singleton
    @Provides
    fun provideSessionManager(preferences: SharedPreferences?) =
        SessionManager(preferences)

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences? =
        context.getSharedPreferences(
            AppConstants.PREF_NAME, Context.MODE_PRIVATE
        )

}

