package com.example.glmediafilter.filter

import android.content.Context
import android.opengl.GLES20
import com.example.glmediafilter.R

class SoulFilter(context: Context) : AbsFBOFilter(context, R.raw.soul_vert, R.raw.soul_frag) {

    private var scalePercent = 1
    private var mixturePercent = 1
    private var scale = 0.0f
    private var mix = 0.0f // 透明度

    init {
        // 从GPU拿到变量的句柄
        scalePercent = GLES20.glGetUniformLocation(program, "scalePercent")
        mixturePercent = GLES20.glGetUniformLocation(program, "mixturePercent")
    }

    override fun beforeDraw() {
        super.beforeDraw()
        GLES20.glUniform1f(scalePercent, scale + 1.0f)
        GLES20.glUniform1f(mixturePercent, 1.0f - mix)
        scale += 0.08f
        mix += 0.08f
        if (scale >= 1.0f) scale = 0.0f
        if (mix >= 1.0) mix = 0.0f
    }

    override fun onDrawFrame(textureId: Int): Int {
        super.onDrawFrame(textureId)
        return frameTexture!![0]
    }
}