package com.example.glmediafilter.filter

import android.content.Context
import android.opengl.GLES20
import android.util.Log
import com.example.glmediafilter.R

class CameraFilter(context: Context) : AbsFBOFilter(context, R.raw.camera_vert, R.raw.camera_frag) {

    private lateinit var mtx: FloatArray
    private var vMatrix = -1
    private val tag="CameraFilter"

    init {
        vMatrix = GLES20.glGetUniformLocation(program, "vMatrix")
    }

    override fun setTransformMatrix(mtx: FloatArray) {
        this.mtx =mtx
    }

    override fun beforeDraw() {
        super.beforeDraw()
        GLES20.glUniformMatrix4fv(vMatrix, 1, false, mtx, 0)
    }

    override fun onDrawFrame(textureId: Int): Int {
        Log.d(tag, "CameraFilter onDrawFrame, thread:${Thread.currentThread().name}")
        return super.onDrawFrame(textureId)
    }
}