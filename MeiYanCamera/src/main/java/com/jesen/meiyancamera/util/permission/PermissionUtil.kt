package com.jesen.meiyancamera.util.permission

import android.Manifest
import android.content.Context
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.ComponentActivity
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.core.content.PermissionChecker.checkSelfPermission

class PermissionUtil(): ActivityCompat.OnRequestPermissionsResultCallback {
    private val REQUEST_VIDEO_PERMISSIONS = 1
    private val VIDEO_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)

    private val FRAGMENT_DIALOG = "dialog"

    private fun shouldShowRequestPermissionRationale( activity: ComponentActivity,permissions: Array<String>) =
        permissions.any { activity.shouldShowRequestPermissionRationale(it) }

    /**
     * Requests permissions needed for recording video.
     */
     fun requestVideoPermissions(activity: ComponentActivity) {
        if (shouldShowRequestPermissionRationale(activity,VIDEO_PERMISSIONS)) {
            Toast.makeText(activity,"权限已经获取",Toast.LENGTH_SHORT).show()
        } else {
            requestPermissions(activity,VIDEO_PERMISSIONS, REQUEST_VIDEO_PERMISSIONS)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_VIDEO_PERMISSIONS) {
            if (grantResults.size == VIDEO_PERMISSIONS.size) {
                for (result in grantResults) {
                    if (result != PERMISSION_GRANTED) {
                        break
                    }
                }
            } else {

            }
        }
    }

    private fun hasPermissionsGranted(context:Context, permissions: Array<String>) =
        permissions.none {
            checkSelfPermission(context, it) != PERMISSION_GRANTED
        }
}