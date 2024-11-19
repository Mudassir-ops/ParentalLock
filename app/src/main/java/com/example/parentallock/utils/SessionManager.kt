package com.example.parentallock.utils

import android.content.SharedPreferences
import android.util.Log
import com.parental.control.displaytime.kids.safety.R
import com.example.parentallock.utils.AppConstants.IS_SERVICE_RUNNING
import com.example.parentallock.utils.AppConstants.SELECTED_LOCK_TYPE
import com.example.parentallock.utils.AppConstants.SELECTED_LOTTIE_RESOURCE_KEY
import com.example.parentallock.utils.AppConstants.SELECTED_POS
import com.example.parentallock.utils.AppConstants.SELECTED_SOUND_RESOURCE_KEY
import javax.inject.Inject

class SessionManager @Inject constructor(private val preferences: SharedPreferences?) {

    fun getSelectedLottieResource(): Int? {
        return preferences?.getInt(
            SELECTED_LOTTIE_RESOURCE_KEY,
            R.raw.instant_lock_danger_animation
        )
    }

    fun setSelectedLottieResource(resourceId: Int) {
        val editor = preferences?.edit()
        editor?.putInt(SELECTED_LOTTIE_RESOURCE_KEY, resourceId)
        editor?.apply()
    }

    fun getSelectedSoundResource(): Int? {
        return preferences?.getInt(
            SELECTED_SOUND_RESOURCE_KEY,
            R.raw.police_siren
        )
    }

    fun setSelectedSoundResource(resourceId: Int) {
        val editor = preferences?.edit()
        editor?.putInt(SELECTED_SOUND_RESOURCE_KEY, resourceId)
        editor?.apply()
    }

    fun getSelectedLockType(): Boolean? {
        return preferences?.getBoolean(
            SELECTED_LOCK_TYPE,
            false
        )
    }

    fun setSelectedLockType(lockType: Boolean) {
        val editor = preferences?.edit()
        editor?.putBoolean(SELECTED_LOCK_TYPE, lockType)
        editor?.apply()
    }

    fun setServiceRunning(isRunning: Boolean) {
        val editor = preferences?.edit()
        editor?.putBoolean(IS_SERVICE_RUNNING, isRunning)
        editor?.apply()
    }

    fun isServiceRunning(): Boolean? {
        return preferences?.getBoolean(IS_SERVICE_RUNNING, false)
    }

    fun setSelectedResourcePos(pos: Int) {
        val editor = preferences?.edit()
        editor?.putInt(SELECTED_POS, pos)
        editor?.apply()
        Log.d("SessionManager", "Saved position: $pos")
    }

    fun getSelectedResourcePos(): Int? {
        return preferences?.getInt(
            SELECTED_POS,
            0
        )
    }

}