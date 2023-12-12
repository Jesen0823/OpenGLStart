package com.example.glmediafilter.render

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import com.example.glmediafilter.CameraXHelper


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
    private  val renderer: CameraRender = CameraRender(context,callback)

    init {
        setEGLContextClientVersion(2)
        setRenderer(renderer)
        // 手动渲染模式
        renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }
    private var mSpeed = Speed.MODE_NORMAL

    enum class Speed {
        MODE_EXTRA_SLOW, MODE_SLOW, MODE_NORMAL, MODE_FAST, MODE_EXTRA_FAST
    }

    fun setSpeed(speed: Speed) {
        mSpeed = speed
    }

    fun startRecord() {
        //速度  时间/速度 speed小于就是放慢 大于1就是加快
        val speed = when (mSpeed) {
            Speed.MODE_EXTRA_SLOW -> 0.3f
            Speed.MODE_SLOW -> 0.5f
            Speed.MODE_NORMAL -> 1f
            Speed.MODE_FAST -> 2f
            Speed.MODE_EXTRA_FAST -> 3f
            else ->1f
        }
        renderer.startRecord(speed)
    }

    private fun setUpCamera() {
        mCameraHelper.startCamera(context,renderer)
    }

    fun stopRecord(){
        renderer.stopRecord()
    }

    fun release(){
        renderer.stopRecord()
        renderer.release()
    }
}