package com.jesen.openglstart.face_meyan.filter;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import com.jesen.openglstart.R;
import com.jesen.openglstart.face_meyan.utils.OpenGLUtils;

/**
 * 主要用来获取摄像头的数据，并创建FBO,在FBO创建特效
 * */
public class CameraFilter extends AbstractFilter{

    int[] mFrameBuffer;
    private int[] mFrameBufferTexture;
    private float[] matrix;

    public CameraFilter(Context context) {
        super(context, R.raw.camera_vertex, R.raw.camera_frag);
    }

    // 坐标转换


    @Override
    protected void initCoordinate() {
        mTextureBuffer.clear();
        // 摄像头是颠倒镜像的（90度）
        // 布局坐标
        /*float[]TEXTURE = {
                0.0f, 0.0f,
                1.0f,0.0f,
                0.0f,1.0f,
                1.0f,1.0f,
        };*/
        float[]TEXTURE = {
                0.0f,0.0f,
                0.0f,1.0f,
                1.0f, 0.0f,
                1.0f,1.0f,
        };
        mTextureBuffer.put(TEXTURE);


    }

    public void onReady(int width,int height){
        super.onReady(width,height);
        mFrameBuffer = new int[1];
        // 生成FBO
        GLES20.glGenTextures(1, mFrameBuffer,0);
        // 纹理，用来绑定FBO,绑定后可以直接对纹理操作
        mFrameBufferTexture = new int[1];
        OpenGLUtils.glGenTextures(mFrameBufferTexture);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,mFrameBufferTexture[0]);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,mFrameBuffer[0]);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D,0,GLES20.GL_RGBA,mWidth,mHeight,0,
                GLES20.GL_RGBA,GLES20.GL_UNSIGNED_BYTE,null);

        // 纹理与FBO联系
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER,GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D,mFrameBufferTexture[0],0);

        // 解绑
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,0);
    }

    // 传递摄像头的矩阵
    public void setMtrix(float[] mrt){
        this.matrix = mrt;
    }

    @Override
    public int onDrawFrame(int textureId) {
        //设置显示窗口
        GLES20.glViewport(0, 0, mWidth, mHeight);

        // 绑定FBO，后面的执行会执行到FBO
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,mFrameBuffer[0]);

        //使用着色器
        GLES20.glUseProgram(mProgram);
        mVertexBuffer.position(0);
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(vPosition);

        mTextureBuffer.position(0);
        GLES20.glVertexAttribPointer(vCoord, 2, GLES20.GL_FLOAT, false, 0, mTextureBuffer);
        GLES20.glEnableVertexAttribArray(vCoord);

        // 变换矩阵,传递给camera_vert.vert
        GLES20.glUniformMatrix4fv(vMatrix,1,false,matrix,0);
        // 不一样的地方
        // 激活
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        // 绑定
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);

        GLES20.glUniform1i(vTexture, 0);
        // 绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);



        // 解绑
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,0);
        // 丢给下一个滤镜
        return mFrameBufferTexture[0];
    }
}
