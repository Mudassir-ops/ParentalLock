import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import com.example.parentallock.data.model.AllAppsEntity
import com.example.parentallock.data.model.DeviceAppEntity
import com.example.parentallock.utils.SafeClickListener

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}

fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
    val safeClickListener = SafeClickListener {
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)
}

fun PackageManager?.getPackageInfoCompat(packageName: String, flags: Int = 0): PackageInfo? =
    try {
        if (SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this?.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(flags.toLong()))
        } else {
            (this?.getPackageInfo(packageName, flags))
        }
    } catch (e: PackageManager.NameNotFoundException) {
        // Handle the case where the package is not found
        null
    }

fun AllAppsEntity.toDeviceAppEntity(): DeviceAppEntity {
    return DeviceAppEntity(
        appName = this.appName ?: "",
        pName = this.pName ?: "",
        versionName = this.versionName ?: "",
        versionCode = this.versionCode,
        icon = null,
        installationDate = this.installationDate
    )
}

