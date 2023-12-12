package com.example.glmediafilter.filter

import android.content.Context
import android.util.Log
import com.example.glmediafilter.R

class RecordFilter(context: Context) : AbsFBOFilter(context, R.raw.base_vert, R.raw.base_frag) {
    private val tag="RecordFilter"

    override fun onDrawFrame(textureId: Int): Int {
        Log.d(tag, "RecordFilter onDrawFrame, thread:${Thread.currentThread().name}")

        return super.onDrawFrame(textureId)
    }

}