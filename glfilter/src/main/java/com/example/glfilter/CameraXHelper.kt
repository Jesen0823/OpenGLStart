package com.example.glfilter

import android.os.HandlerThread
import android.util.Size
import androidx.camera.core.CameraX
import androidx.camera.core.Preview
import androidx.camera.core.PreviewConfig
import androidx.lifecycle.LifecycleOwner

class CameraXHelper(lifecycleOwner:LifecycleOwner, private val listener: Preview.OnPreviewOutputUpdateListener) {
    private val mHandlerThread:HandlerThread = HandlerThread("Analyze-thread")
    private var currentFacing = CameraX.LensFacing.BACK

    init {
        mHandlerThread.start()
        CameraX.bindToLifecycle(lifecycleOwner,getPreview())
    }

    private fun getPreview(): Preview {
        val previewConfig = PreviewConfig.Builder()
            .setTargetResolution(Size(640,480))
            .setLensFacing(currentFacing)
            .build()
        val preview = Preview(previewConfig)
        preview.onPreviewOutputUpdateListener = listener
        return preview
    }
}