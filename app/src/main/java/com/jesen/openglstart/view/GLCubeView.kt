package com.jesen.openglstart.view

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import com.jesen.openglstart.view.FGLRender

/**
 * 第一个OpenGL的自定义View
 */
class GLCubeView : GLSurfaceView {

    constructor(context: Context?) : super(context) {}

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        setEGLContextClientVersion(2) // 使用的版本

        // 设置渲染工作执行者
        setRenderer(FGLCubeRender(this))
        requestRender()
        // 第二种方式，使用时渲染，需要主动requestRender()
        renderMode = RENDERMODE_WHEN_DIRTY

        // 第一种方式，每隔一段时间渲染一次,不需要requestRender()
        //setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }
}










