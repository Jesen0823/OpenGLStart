package com.jesen.meiyancamera.filter;

import android.content.Context;
import android.opengl.GLES20;

import com.jesen.meiyancamera.util.OpenGLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public abstract class AbstractFilter {
    // 顶点着色
    protected int mVertexShaderId;
    // 片段着色器
    protected int mFragShaderId;

    protected FloatBuffer mTextureBuffer;
    protected FloatBuffer mVertexBuffer;

    protected int vTexture;
    // vMatrix  矩阵
    protected int vMatrix;
    protected int vCoord;
    protected int vPosition;
    protected int mProgram;
    protected int mWidth;
    protected int mHeight;

    public void onReady(int width,int height){
        this.mWidth = width;
        this.mHeight = height;
    }

    public AbstractFilter(Context context, int vertexShaderId, int fragShaderId){
        this.mVertexShaderId = vertexShaderId;
        this.mFragShaderId =fragShaderId;

        // 4个顶点每个顶点两个值，每个值float4字节
        mVertexBuffer = ByteBuffer.allocateDirect(4*2*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertexBuffer.clear();
        float[] VERTEX = {
                -1.0f,-1.0f,
                1.0f,-1.0f,
                -1.0f,1.0f,
                1.0f,1.0f
        };
        mVertexBuffer.put(VERTEX);

        mTextureBuffer = ByteBuffer.allocateDirect(4*2*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTextureBuffer.clear();
        float[] TEXTURE = {
                0.0f, 1.0f,
                1.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f
        };
        mTextureBuffer.put(TEXTURE);
        initilize(context);
        initCoordinate();
    }

    protected abstract void initCoordinate();

    private void initilize(Context context) {
        String vertexSharder = OpenGLUtils.readRawFile(context, mVertexShaderId);
        String framentShader = OpenGLUtils.readRawFile(context, mFragShaderId);

        mProgram = OpenGLUtils.loadProgram(vertexSharder, framentShader);
//        获取vPosition
        // 获得着色器中的 attribute 变量 position 的索引值
        vPosition = GLES20.glGetAttribLocation(mProgram, "vPosition");
        vCoord = GLES20.glGetAttribLocation(mProgram,
                "vCoord");
        vMatrix = GLES20.glGetUniformLocation(mProgram,
                "vMatrix");
        // 获得Uniform变量的索引值
        vTexture = GLES20.glGetUniformLocation(mProgram,
                "vTexture");
    }

    public int onDrawFrame(int textureId) {
        //设置显示窗口
        GLES20.glViewport(0, 0, mWidth, mHeight);

        //使用着色器
        GLES20.glUseProgram(mProgram);
        mVertexBuffer.position(0);
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(vPosition);

        mTextureBuffer.position(0);
        GLES20.glVertexAttribPointer(vCoord, 2, GLES20.GL_FLOAT, false, 0, mTextureBuffer);
        GLES20.glEnableVertexAttribArray(vCoord);

//        不一样的地方
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureId);

        GLES20.glUniform1i(vTexture, 0);
//        绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        return textureId;
    }
}
