package com.jesen.meiyancamera.view;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.jesen.meiyancamera.filter.CameraFilter;
import com.jesen.meiyancamera.filter.ScreenFilter;
import com.jesen.meiyancamera.util.CameraHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MeiyanRenderer implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {
    public CameraHelper mCameraHelper;
    private MeiyanView mView;
    private SurfaceTexture mSurfaceTexture;
    private int[] mTextures;
    private float[] mtx = new float[16];

    CameraFilter mCameraFilter;
    ScreenFilter mScreenFilter;

    public MeiyanRenderer(MeiyanView view){
        this.mView = view;
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        mView.requestRender();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // 打开摄像头
        // 初始化的操作
        mCameraHelper = new CameraHelper(Camera.CameraInfo.CAMERA_FACING_BACK);
        // 纹理   ==数据的输入
        mTextures = new int[1];
        GLES20.glGenTextures(mTextures.length,mTextures, 0);
        mSurfaceTexture = new SurfaceTexture(mTextures[0]);
        mSurfaceTexture.setOnFrameAvailableListener(this);
        // 矩阵--》   摄像头的数据 不会变形  顶点
        mSurfaceTexture.getTransformMatrix(mtx);
        mCameraFilter = new CameraFilter(mView.getContext());
        mScreenFilter = new ScreenFilter(mView.getContext());
        mCameraFilter.setMatrix(mtx);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mCameraHelper.startPreview(mSurfaceTexture);
        mCameraFilter.onReady(width,height);
        mScreenFilter.onReady(width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // 摄像头获取一帧数据   会回调此方法
        GLES20.glClearColor(0, 0, 0, 0);
        // 执行清空
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        mSurfaceTexture.updateTexImage();
        mSurfaceTexture.getTransformMatrix(mtx);
        mCameraFilter.setMatrix(mtx);
        // 信息  int   类型---》GPU 纹理  Request    ---》返回 一个新的   Request
        int id=mCameraFilter.onDrawFrame(mTextures[0]);
        // id ==效果1.onDrawFrame(id);  帽子
        // id ==效果2.onDrawFrame(id);  眼镜
        // id ==效果2.onDrawFrame(id);  大耳朵

        // mScreenFilter 将最终的特效运用到SurfaceView中
        mScreenFilter.onDrawFrame(id);
    }
}
