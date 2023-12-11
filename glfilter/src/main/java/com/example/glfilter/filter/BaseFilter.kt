package com.example.glfilter.filter

interface BaseFilter {
    fun onDrawFrame(textureId: Int): Int
    fun setTransformMatrix(mtx: FloatArray)
    fun onReady(width: Int, height: Int)
    fun release()
}