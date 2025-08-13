package io.github.ackeecz.ackeelities.core

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.core.net.toUri

public fun Context.isPermissionGranted(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

public fun Context.areAllPermissionsGranted(permissions: Array<String>): Boolean {
    return permissions.all { isPermissionGranted(it) }
}

/**
 * Navigates to the application settings screen.
 *
 * @param newTask If true, the activity will be started in a new task. This is explicitly needed
 * in the case you try to navigate from the non-activity context. In this case it crashes, which mirrors
 * the behavior of the Android system when you try to start an activity from a non-activity context.
 */
public fun Context.navigateToAppSettings(newTask: Boolean = false) {
    val uri = Uri.fromParts("package", packageName, null)
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri).apply {
        if (newTask) {
            flags += Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }
    startActivity(intent)
}

/**
 * Opens Google Play with the application ID from provided context. Return true if the intent can be
 * and will be handled, false otherwise.
 */
public fun Context.openGooglePlay(): Boolean {
    val intent = Intent(Intent.ACTION_VIEW, "market://details?id=$packageName".toUri())
    val canBeOpened = packageManager.queryIntentActivities(intent, 0).isNotEmpty()
    if (canBeOpened) {
        startActivity(intent)
    }
    return canBeOpened
}
