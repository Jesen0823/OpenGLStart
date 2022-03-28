package com.jesen.meiyancamera;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.jesen.meiyancamera.util.CameraHelper;
import com.jesen.meiyancamera.util.permission.PermissionUtil;
import com.jesen.meiyancamera.view.MeiyanView;

public class MeiYanActivity extends AppCompatActivity {

    private View switchCamera;
    private MeiyanView cView;
    private CameraHelper cameraHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meiyan);

        switchCamera = findViewById(R.id.switchCamera);
        cView = findViewById(R.id.mSurface);

        PermissionUtil permission = new PermissionUtil();
        permission.requestVideoPermissions(this);

        switchCamera.setOnClickListener(v -> {
            cView.renderer.mCameraHelper.switchCamera();
        });
    }
}