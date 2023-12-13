package com.example.glmediafilter.filter

import android.content.Context
import android.opengl.GLES20
import com.example.glmediafilter.R

class SpiltFilter(context: Context) : AbsFBOFilter(context, R.raw.split_vert, R.raw.split2_screen) {

    override fun onDrawFrame(textureId: Int): Int {
        super.onDrawFrame(textureId)
        return frameTexture!![0]
    }
}