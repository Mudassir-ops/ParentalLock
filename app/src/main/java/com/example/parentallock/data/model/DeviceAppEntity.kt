package com.example.parentallock.data.model

import android.graphics.drawable.Drawable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import javax.annotation.Nonnull

@Entity(tableName = "package_info_table")
data class DeviceAppEntity(
    var appName: String = "",
    @PrimaryKey
    @Nonnull
    var pName: String = "",
    var versionName: String = "",
    var versionCode: Int = 0,
    var installationDate: String?,
    @Ignore
    var icon: Drawable? = null
) {
    constructor() : this("", "", "", 0, "", null)
}
