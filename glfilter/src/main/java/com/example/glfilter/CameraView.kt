package com.example.glfilter

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

/**
 * 运行在gl-thread线程
 */
class CameraView(context: Context,attr: AttributeSet) : GLSurfaceView(context,attr) {
    private lateinit var renderer:CameraRender

    init {
        setEGLContextClientVersion(2)
        renderer = CameraRender(this)
        setRenderer(renderer)
        // 手动渲染模式
        renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }

    fun startRender(){

    }
}