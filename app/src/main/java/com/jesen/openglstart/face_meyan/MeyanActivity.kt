package com.jesen.openglstart.face_meyan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jesen.openglstart.R
import com.jesen.openglstart.face_meyan.utils.PermissionUtil
import java.util.jar.Manifest

class MeyanActivity : AppCompatActivity() {

    private val perms = arrayOf(android.Manifest.permission.CAMERA)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meyan)


        PermissionUtil.checkPermissions(this,perms, Runnable {

        })
    }
}