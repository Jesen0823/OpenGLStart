package com.example.glmediafilter.render

import android.content.Context
import android.opengl.EGL14
import android.opengl.EGLConfig
import android.opengl.EGLContext
import android.opengl.EGLDisplay
import android.opengl.EGLExt
import android.opengl.EGLSurface
import android.view.Surface
import com.example.glmediafilter.filter.ScreenFilter

/**
 * 访问GPU数据
 */
class EGLEnv(context: Context, gLContext: EGLContext, surface: Surface, width: Int, height: Int) {

    // 屏幕
    private val mEglDisplay: EGLDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
    private var mEglConfig: EGLConfig? = null

    // 离屏Surface
    private var mEglSurface: EGLSurface? = null
    private var mEglContext: EGLContext? = null
    private var screenFilter: ScreenFilter

    init {
        if (mEglDisplay == EGL14.EGL_NO_DISPLAY) {
            throw RuntimeException("mEglDisplay create failed.")
        }
        val version = IntArray(2)
        if (!EGL14.eglInitialize(mEglDisplay, version, 0, version, 1)) {
            throw RuntimeException("mEglDisplay init failed.")
        }

        // 配置 属性选项
        val configAttribs = intArrayOf(
            EGL14.EGL_RED_SIZE, 8,  //颜色缓冲区中红色位数
            EGL14.EGL_GREEN_SIZE, 8,  //颜色缓冲区中绿色位数
            EGL14.EGL_BLUE_SIZE, 8,
            EGL14.EGL_ALPHA_SIZE, 8,
            EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,  //opengl es 2.0
            EGL14.EGL_NONE
        )
        val numConfigs = IntArray(1)
        val configs: Array<EGLConfig?> = arrayOfNulls<EGLConfig>(1)
        //EGL 根据属性选择一个配置
        if (!EGL14.eglChooseConfig(
                mEglDisplay, configAttribs, 0, configs, 0, configs.size,
                numConfigs, 0
            )
        ) {
            throw RuntimeException("EGL error " + EGL14.eglGetError())
        }
        mEglConfig = configs[0]
        val contextAttribList = intArrayOf(
            EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
            EGL14.EGL_NONE
        )

        // 创建好了之后可以读取到数据，需要绑定EGLSurface
        mEglContext =
            EGL14.eglCreateContext(mEglDisplay, mEglConfig, gLContext, contextAttribList, 0)

        if (mEglContext == EGL14.EGL_NO_CONTEXT) {
            throw RuntimeException("EGL mEglContext error " + EGL14.eglGetError())
        }
        // 创建离屏EGLSurface
        val surfaceAttribList = intArrayOf(EGL14.EGL_NONE)
        mEglSurface =
            EGL14.eglCreateWindowSurface(mEglDisplay, mEglConfig, surface, surfaceAttribList, 0)

        if (mEglSurface == null) {
            throw RuntimeException("EGL mEglSurface error " + EGL14.eglGetError())
        }

        // 绑定当前线程的显示器display mEglDisplay  虚拟 物理设备
        if (!EGL14.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext)) {
            throw RuntimeException("EGL eglMakeCurrent error " + EGL14.eglGetError())
        }
        screenFilter = ScreenFilter(context)
        screenFilter.setSize(width, height)
    }

    fun draw(textureId: Int, timestamp: Long) {
        // 调用drawFrame,FBO数据渲染到mEglSurface离屏
        screenFilter.onDrawFrame(textureId)
        // 给帧缓冲时间戳
        EGLExt.eglPresentationTimeANDROID(mEglDisplay, mEglSurface, timestamp)
        // EGLSurface双缓冲交换数据
        EGL14.eglSwapBuffers(mEglDisplay, mEglSurface)
    }

    fun release() {
        EGL14.eglDestroySurface(mEglDisplay, mEglSurface)
        EGL14.eglMakeCurrent(
            mEglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE,
            EGL14.EGL_NO_CONTEXT
        )
        EGL14.eglDestroyContext(mEglDisplay, mEglContext)
        EGL14.eglReleaseThread()
        EGL14.eglTerminate(mEglDisplay)
        screenFilter.release()
    }
}