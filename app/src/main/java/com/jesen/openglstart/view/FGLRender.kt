package com.jesen.openglstart.view

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class FGLRender(glView: GLView) : GLSurfaceView.Renderer {

    val mView = glView
    lateinit var shape:Triangle

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        GLES20.glClearColor(0F,0F,0F,0F); // 清空
        shape = Triangle()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        shape.onSurfaceChanged(gl,width,height)
    }

    // 绘制，不断地被调用
    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        shape.onDrawFrame(gl)
    }

}