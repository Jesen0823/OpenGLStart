package com.example.glmediafilter.filter

import android.content.Context
import android.opengl.GLES20

open class AbsFBOFilter(context: Context, vertRes: Int, fragRes: Int) :
    SimpleFilter(context, vertRes, fragRes) {

    private var frameBuffer: IntArray? = null
    private var frameTexture: IntArray? = null

    override fun setSize(width: Int, height: Int) {
        super.setSize(width, height)
        releaseFrame()
        initFBO(width, height)
    }

    // 实例化FBO，让摄像头的数据先渲染到FBO
    private fun initFBO(width: Int, height: Int) {
        frameBuffer = IntArray(1)
        // 创建FBO
        GLES20.glGenFramebuffers(1, frameBuffer, 0)
        // 生成纹理
        frameTexture = IntArray(1)
        GLES20.glGenTextures(frameTexture!!.size, frameTexture, 0)
        // 绑定
        for (i in 0..<frameTexture!!.size) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, frameTexture!![i])
            // 放大过滤
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_NEAREST
            )
            // 缩小过滤
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR
            )
            // 告诉GPU纹理操作完成
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        }
        // 准备绑定
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, frameTexture!![0])

        GLES20.glTexImage2D(
            GLES20.GL_TEXTURE_2D,
            0,
            GLES20.GL_RGBA,
            width, height,
            0,
            GLES20.GL_RGBA,
            GLES20.GL_UNSIGNED_BYTE,
            null
        )
        // 使用GPU的FBO数据区,正式绑定FBO
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer!![0])
        // 真的真的绑定FBO和纹理
        GLES20.glFramebufferTexture2D(
            GLES20.GL_FRAMEBUFFER,
            GLES20.GL_COLOR_ATTACHMENT0,
            GLES20.GL_TEXTURE_2D,
            frameTexture!![0],
            0
        )

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
    }

    private fun releaseFrame() {
        frameTexture?.let {
            GLES20.glDeleteTextures(1, it, 0)
            frameTexture = null
        }
        frameBuffer?.let {
            GLES20.glDeleteFramebuffers(1, it, 0)
            frameBuffer = null
        }
    }

    override fun onDrawFrame(textureId: Int): Int {
        // 渲染到FBO,数据进入frameBuffer
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer!![0])
        super.onDrawFrame(textureId)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)

        return frameTexture!![0]
    }
}