package com.jesen.openglstart.view;

import android.opengl.GLES20;
import android.opengl.Matrix;

import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Triangle {

    int mProgram;

    // 用于创建顶点着色器的语句，类似native函数
    private String vertextShaderCode =
            "attribute vec4 vPosition;\n" +
                    "uniform mat4 vMatrix;\n" +
                    "void main(){\n" +
                    "gl_Position=vMatrix * vPosition;\n" +
                    "}";

    // 片元着色器
    private final String fragmentShaderCode = "precision mediump float;\n" +
            "uniform vec4 vColor;\n" +
            "void main(){\n" +
            "gl_FragColor=vColor;\t\n" +
            "}";
    // 初始化

    static float triangleCoords[] = {
            0.5f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
    };

    // rgba
    float colors[] = {1.0f, 0.8f, 0.2f, 1.0f};

    private FloatBuffer vertexBuffer;

    // 声明三个矩阵变换参数
    private float[] mViewMatrix = new float[16];
    private float[] mProjectMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];



    public Triangle() {
        // ByteBuffer在GPU里面
        ByteBuffer bb = ByteBuffer.allocateDirect(triangleCoords.length * 4); //一个float是4字节
        bb.order(ByteOrder.nativeOrder());

        vertexBuffer = bb.asFloatBuffer(); // 转换为管道
        vertexBuffer.put(triangleCoords); // 告诉GPU
        vertexBuffer.position(0);

        // 创建顶点着色器,在GPU编译
        int shader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        // 发送顶点程序源码
        GLES20.glShaderSource(shader, vertextShaderCode);
        // 编译
        GLES20.glCompileShader(shader);

        // 创建片元着色器,在GPU编译
        int fragShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fragShader, fragmentShaderCode);
        GLES20.glCompileShader(fragShader);

        // 两个着色器放入统一程序管理
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, shader);
        GLES20.glAttachShader(mProgram, fragShader);
        // 链接到着色器程序
        GLES20.glLinkProgram(mProgram);
    }

    // 渲染
    public void onDrawFrame(@Nullable GL10 gl) {
        GLES20.glUseProgram(mProgram);

        // 传入矩阵到参数"vMatrix"
        int mMatrixHandler = GLES20.glGetUniformLocation(mProgram,"vMatrix");
        GLES20.glUniformMatrix4fv(mMatrixHandler,1,false,mMVPMatrix,0);
        // 得到vPosition，将顶点数据vertexBuffer塞进去
        int mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        // 类似于锁
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        // 顶点数量3个，stride一行的数据字节量 3*vec4
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false,
                3 * 4, vertexBuffer);


        // 处理颜色
        int mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        //GLES20.glUniform4fv(mColorHandle, 1, colors, 0);
        GLES20.glUniform4fv(mColorHandle, 1, colors, 0);
        // 最终绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
        // 类似解锁
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

    // 矩阵坐标转换写法
    public void onSurfaceChanged(GL10 gl,int width, int height) {
        // 宽高比
        float ratio = 1.f * width/height;
        // 投影矩阵，投影的面
        Matrix.frustumM(mProjectMatrix,0,-ratio,ratio,-1,1,3,120);
        // 相机（观察者）
        Matrix.setLookAtM(mViewMatrix,0,0,0,7, // 相机坐标
                0f,0f, 0f,  // 目标物中心坐标
                0f, 1.0f,0.0f); // 相机观察的方向
        // 计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix,0,mViewMatrix,0);
        // 可有可不有
        GLES20.glViewport(0,0,width,height);
    }
}
