package com.jesen.openglstart.face_meyan;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.View;

import com.jesen.openglstart.face_meyan.filter.CameraFilter;
import com.jesen.openglstart.face_meyan.filter.ScreenFilter;
import com.jesen.openglstart.face_meyan.utils.CameraHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MeyanRender implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    private CameraHelper mCameraHelper;
    private MeyanView mView;
    private SurfaceTexture mSurfaceTexture; // 桥梁作用，可以从摄像头传递数据到OpenGL
    private int[] mTextures; //纹理,用来从摄像头取数据
    private float[] mtx = new float[16];

    private CameraFilter mCameraFilter;
    private ScreenFilter mScreenFilter;

    public MeyanRender(MeyanView view) {
        mView = view;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        // 打开摄像头
        mCameraHelper = new CameraHelper(Camera.CameraInfo.CAMERA_FACING_BACK);
        // 纹理创建
        mTextures = new int[1];
        GLES20.glGenTextures(mTextures.length, mTextures, 0);

        mSurfaceTexture = new SurfaceTexture(mTextures[0]);
        mSurfaceTexture.setOnFrameAvailableListener(this);

        // 矩阵保证了摄像头画面不变形
        mSurfaceTexture.getTransformMatrix(mtx);
        mCameraFilter = new CameraFilter(mView.getContext());
        mScreenFilter = new ScreenFilter(mView.getContext());

        mCameraFilter.setMtrix(mtx);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        mCameraHelper.startPreview(mSurfaceTexture);
        mCameraFilter.onReady(width,height);
        mScreenFilter.onReady(width,height);
    }

    // 摄像头数据的回调
    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        // 请求OPenGL处理，OpenGL会回调onDrawFrame
        mView.requestRender();
    }


    @Override
    public void onDrawFrame(GL10 gl) {
        // 清空GPU
        GLES20.glClearColor(0, 0, 0, 0);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        mSurfaceTexture.updateTexImage();
        mSurfaceTexture.getTransformMatrix(mtx);
        mCameraFilter.setMtrix(mtx);

        // 将纹理丢给滤镜1处理，处理完生成纹理2(FBO)，再丢给滤镜2处理
        int id =  mCameraFilter.onDrawFrame(mTextures[0]); // 美白
        // int id2 = mCameraFilter.onDrawFrame(id); // 瘦脸
        // int id3 = mCameraFilter.onDrawFrame(id2); // 变大
        // ... ...
        // 最后交给显示相关 mScreenFilter
        mScreenFilter.onDrawFrame(id);


    }
}
