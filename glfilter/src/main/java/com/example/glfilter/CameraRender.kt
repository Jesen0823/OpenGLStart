package com.example.glfilter

import android.graphics.SurfaceTexture
import android.opengl.GLSurfaceView
import android.util.Log
import androidx.camera.core.Preview
import androidx.lifecycle.LifecycleOwner
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CameraRender(private val cameraView: CameraView) : GLSurfaceView.Renderer,
    Preview.OnPreviewOutputUpdateListener, SurfaceTexture.OnFrameAvailableListener {
    private val tag = "CameraRender"
    private var mCameraXHelper: CameraXHelper
    private var mCameraTexture: SurfaceTexture? = null
    private lateinit var screenFilter: ScreenFilter
    private lateinit var textures: IntArray
    private val mtx = FloatArray(16)

    init {
        val lifecycleOwner = cameraView.context as LifecycleOwner
        mCameraXHelper = CameraXHelper(lifecycleOwner, this)
        screenFilter = ScreenFilter(cameraView.context)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        Log.d(tag, "onSurfaceCreated, thread:${Thread.currentThread().name}")
        textures = IntArray(1)
        // 让 SurfaceTexture与Gpu共享一个数据源
        mCameraTexture?.attachToGLContext(textures[0])
        mCameraTexture?.setOnFrameAvailableListener(this)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        screenFilter.setSize(width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        Log.d(tag, "onDrawFrame, thread:${Thread.currentThread().name}")
        // 此时更新摄像头的数据已经给了gpu
        mCameraTexture?.updateTexImage()
        mCameraTexture?.getTransformMatrix(mtx)
        screenFilter.setTransformMatrix(mtx)
        screenFilter.onDraw(textures[0])
    }

    override fun onUpdated(output: Preview.PreviewOutput?) {
        // 摄像头预览数据
        mCameraTexture = output?.surfaceTexture
    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        cameraView.requestRender()
    }
}