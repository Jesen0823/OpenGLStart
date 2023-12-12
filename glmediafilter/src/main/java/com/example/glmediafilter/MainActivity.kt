package com.example.glmediafilter

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.RadioGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.glmediafilter.render.CameraView
import com.example.glmediafilter.utils.RecordButton


class MainActivity : AppCompatActivity(), RecordButton.OnRecordListener,
    RadioGroup.OnCheckedChangeListener {
    private val multiplePermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}

    @RequiresApi(Build.VERSION_CODES.R)
    private val storageManagerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val isPermissionAllow = Environment.isExternalStorageManager()
            }
        }

    private lateinit var view: CameraView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        view = findViewById(R.id.camera_view)

        findViewById<RecordButton>(R.id.btn_record).setOnRecordListener(this)
        findViewById<RadioGroup>(R.id.rg_speed).setOnCheckedChangeListener(this)

        requestPermissions()
    }


    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                multiplePermission.launch(arrayOf(Manifest.permission.CAMERA))
            }
            if (Environment.isExternalStorageManager()) {
                val isPermissionAllow = true
            } else {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:" + this@MainActivity.packageName)
                storageManagerLauncher.launch(intent)
            }
        } else {
            multiplePermission.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        }
    }

    override fun onDestroy() {
        view.release()
        super.onDestroy()
    }

    override fun onRecordStart() {
        view.startRecord()
    }

    override fun onRecordStop() {
        view.stopRecord()
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            R.id.btn_extra_slow -> view.setSpeed(CameraView.Speed.MODE_EXTRA_SLOW)
            R.id.btn_slow -> view.setSpeed(CameraView.Speed.MODE_SLOW)
            R.id.btn_normal -> view.setSpeed(CameraView.Speed.MODE_NORMAL)
            R.id.btn_fast -> view.setSpeed(CameraView.Speed.MODE_FAST)
            R.id.btn_extra_fast -> view.setSpeed(CameraView.Speed.MODE_EXTRA_FAST)
        }
    }
}