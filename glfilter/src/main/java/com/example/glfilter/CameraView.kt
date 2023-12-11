package com.example.glfilter

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

/**
 * 运行在gl-thread线程
 */
class CameraView(context: Context,attr: AttributeSet) : GLSurfaceView(context,attr) {
    private val mCameraHelper = CameraXHelper()
    private val callback = object : CameraRender.Callback {
        override fun onSurfaceChanged() {
            setUpCamera()
        }

        override fun onFrameAvailable() {
            requestRender()
        }
    }

    private fun setUpCamera() {
        mCameraHelper.startCamera(context,renderer)
    }

    private  val renderer:CameraRender = CameraRender(context,callback)

    init {
        setEGLContextClientVersion(2)
        setRenderer(renderer)
        // 手动渲染模式
        renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }

    fun release(){
        renderer.release()
    }
}