package com.example.glfilter

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import android.util.Size
import android.view.Surface
import androidx.annotation.WorkerThread
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import com.example.glfilter.filter.BaseFilter
import com.example.glfilter.filter.ScreenFilter
import java.util.concurrent.Executors
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CameraRender(private val context: Context, private val callback: Callback) :
    GLSurfaceView.Renderer,
    Preview.SurfaceProvider, SurfaceTexture.OnFrameAvailableListener {

    private val tag = "CameraRender"
    private var mCameraTexture: SurfaceTexture? = null
    private var filter: BaseFilter? = null
    private val textures = IntArray(1)
    private val executor = Executors.newSingleThreadExecutor()
    private val mtx = FloatArray(16)

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        Log.d(tag, "onSurfaceCreated, thread:${Thread.currentThread().name}")
        gl?.let {
            it.glGenTextures(textures.size, textures, 0)
            mCameraTexture = SurfaceTexture(textures[0])
            filter = ScreenFilter(context)
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        callback.onSurfaceChanged()
        filter?.onReady(width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        Log.d(tag, "onDrawFrame, thread:${Thread.currentThread().name}")

        if (gl == null || mCameraTexture == null) return
        gl.glClearColor(0f, 0f, 0f, 0f)
        gl.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        // 此时更新摄像头的数据已经给了gpu
        mCameraTexture!!.updateTexImage()
        mCameraTexture!!.getTransformMatrix(mtx)
        filter?.setTransformMatrix(mtx)
        filter?.onDrawFrame(textures[0])
    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        callback.onFrameAvailable()
    }

    override fun onSurfaceRequested(request: SurfaceRequest) {
        val resetTexture = resetPreviewTexture(request.resolution) ?: return
        val surface = Surface(resetTexture)
        request.provideSurface(surface, executor) {
            surface.release()
            mCameraTexture?.release()
        }
    }

    @WorkerThread
    private fun resetPreviewTexture(size: Size): SurfaceTexture? {
        return mCameraTexture?.let { surfaceTexture ->
            surfaceTexture.setOnFrameAvailableListener(this)
            surfaceTexture.setDefaultBufferSize(size.width, size.height)
            surfaceTexture
        }
    }

    fun release() {
        filter?.release()
    }

    interface Callback {
        fun onSurfaceChanged()
        fun onFrameAvailable()
    }
}