package com.example.glmediafilter.render

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.EGL14
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.os.Environment
import android.util.Log
import android.util.Size
import android.view.Surface
import androidx.annotation.WorkerThread
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import com.example.glmediafilter.MediaRecorder
import com.example.glmediafilter.filter.CameraFilter
import com.example.glmediafilter.filter.RecordFilter
import com.example.glmediafilter.filter.SoulFilter
import com.example.glmediafilter.filter.SpiltFilter
import java.io.File
import java.util.concurrent.Executors
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CameraRender(private val context: Context, private val callback: Callback) :
    GLSurfaceView.Renderer,
    Preview.SurfaceProvider, SurfaceTexture.OnFrameAvailableListener {

    private val tag = "CameraRender"
    private var mCameraTexture: SurfaceTexture? = null
    private var cameraFilter: CameraFilter? = null
    private var soulFilter: SoulFilter? = null
    private var recordFilter: RecordFilter? = null
    private var mediaRecorder: MediaRecorder? = null
    private var spiltFilter:SpiltFilter?=null
    private val textures = IntArray(1)
    private val executor = Executors.newSingleThreadExecutor()
    private val mtx = FloatArray(16)
    private val outFile =
        File(Environment.getExternalStorageDirectory(), "outFilter.mp4")

    fun startRecord(speed: Float) {
        mediaRecorder?.start(speed)
    }

    fun stopRecord() {
        mediaRecorder?.stop()
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        Log.d(tag, "onSurfaceCreated, thread:${Thread.currentThread().name}")
        gl?.let {
            mCameraTexture?.attachToGLContext(textures[0])
            mCameraTexture?.setOnFrameAvailableListener(this)
            it.glGenTextures(textures.size, textures, 0)
            mCameraTexture = SurfaceTexture(textures[0])
            cameraFilter = CameraFilter(context)
            recordFilter = RecordFilter(context)
            soulFilter = SoulFilter(context)
            spiltFilter = SpiltFilter(context)
            if (outFile.exists()) outFile.delete()
            mediaRecorder =
                MediaRecorder(context, outFile.absolutePath, EGL14.eglGetCurrentContext(), 480, 640)
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        callback.onSurfaceChanged()
        recordFilter?.setSize(width, height)
        cameraFilter?.setSize(width, height)
        soulFilter?.setSize(width, height)
        spiltFilter?.setSize(width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        Log.d(tag, "CameraRender onDrawFrame, thread:${Thread.currentThread().name}")

        if (gl == null || mCameraTexture == null) return
        gl.glClearColor(0f, 0f, 0f, 0f)
        gl.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        // 此时更新摄像头的数据已经给了gpu
        mCameraTexture!!.updateTexImage()
        mCameraTexture!!.getTransformMatrix(mtx)
        cameraFilter?.setTransformMatrix(mtx)

        // FBO所在图层的纹理id
        val cfID = cameraFilter?.onDrawFrame(textures[0]) // 特效
        val slID = soulFilter?.onDrawFrame(cfID!!) // 特效
        val spID = spiltFilter?.onDrawFrame(slID!!)
        val edID = recordFilter?.onDrawFrame(spID!!) // 无特效，渲染摄像头
        // 主动调用OenGL的数据，编码存储
        mediaRecorder?.fireFrame(edID!!, mCameraTexture!!.timestamp)
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
        mediaRecorder?.stop()
        cameraFilter?.release()
    }

    interface Callback {
        fun onSurfaceChanged()
        fun onFrameAvailable()
    }
}