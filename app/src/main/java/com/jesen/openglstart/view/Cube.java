package com.jesen.openglstart.view;

import android.opengl.GLES20;
import android.opengl.Matrix;

import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Cube {

    int mProgram;

    // 用于创建顶点着色器的语句，类似native函数
    private String vertextShaderCode =
            "attribute vec4 vPosition;\n" +
                    "uniform mat4 vMatrix;\n" +
                    "varying vec4 vColor;\n" +
                    "attribute vec4 aColor;\n" +
                    "void main(){\n" +
                    "gl_Position=vMatrix*vPosition;\n" +
                    "vColor=aColor;\n" +
                    "}";

    // 片元着色器
    private final String fragmentShaderCode = "precision mediump float;\n" +
            "varying vec4 vColor;\n" +
            "void main(){\n" +
            "gl_FragColor=vColor;\t\n" +
            "}";
// 初始化

    static float cubePositions[] = {
            1.0f, 1.0f, 1.0f, // 正面左上0
            -1.0f, -1.0f, 1.0f, // 正面左下1
            1.0f, -1.0f, 1.0f, // 正面右下2
            1.0f, 1.0f, 1.0f,  //正面石上3
            -1.0f, 1.0f, -1.0f, //反面左上4
            -1.0f, -1.0f, -1.0f, // 反面左下5
            1.0f, -1.0f, -1.0f, // 反面右下6
            1.0f, 1.0f, -1.0f,  //反面右上7
    };

    float colors[] = {
            0f, 1f, 0f, 1f,
            0f, 1f, 0f, 1f,
            0f, 1f, 0f, 1f,
            0f, 1f, 0f, 1f,
            1f, 0f, 0f, 1f,
            1f, 0f, 0f, 1f,
            1f, 0f, 0f, 1f,
            1f, 0f, 0f, 1f,
    };

    final short index[] = {
            6, 7, 4, 6, 4, 5, // 后面
            6, 3, 7, 6, 2, 3, // 右面
            6, 5, 1, 6, 1, 2, // 下面
            0, 3, 2, 0, 2, 1, // 正面
            0, 1, 5, 0, 5, 4, // 左面
            0, 7, 3, 0, 4, 7, // 上面
    };

    private FloatBuffer vertexBuffer,colorBuffer;
    private ShortBuffer indexBuffer;

    // 声明三个矩阵变换参数
    private float[] mViewMatrix = new float[16];
    private float[] mProjectMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];


    public Cube() {
        // ByteBuffer在GPU里面
        ByteBuffer bb = ByteBuffer.allocateDirect(cubePositions.length * 4); //一个float是4字节
        bb.order(ByteOrder.nativeOrder());

        vertexBuffer = bb.asFloatBuffer(); // 转换为管道
        vertexBuffer.put(cubePositions); // 告诉GPU
        vertexBuffer.position(0);

        ByteBuffer dd = ByteBuffer.allocateDirect(colors.length*4);
        dd.order(ByteOrder.nativeOrder());
        colorBuffer = dd.asFloatBuffer();
        colorBuffer.put(colors);
        colorBuffer.position(0);

        ByteBuffer cc = ByteBuffer.allocateDirect(index.length*2);
        cc.order(ByteOrder.nativeOrder());
        indexBuffer = cc.asShortBuffer();
        indexBuffer.put(index);
        indexBuffer.position(0);


        // 创建顶点着色器,在GPU编译
        int vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vertexShader, vertextShaderCode);
        GLES20.glCompileShader(vertexShader);

        // 创建片元着色器,在GPU编译
        int fragShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fragShader, fragmentShaderCode);
        GLES20.glCompileShader(fragShader);

        // 两个着色器放入统一程序管理
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragShader);
        // 链接到着色器程序
        GLES20.glLinkProgram(mProgram);
    }

    // 渲染
    public void onDrawFrame(@Nullable GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUseProgram(mProgram);

        // 传入矩阵到参数"vMatrix"
        int mMatrixHandler = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        GLES20.glUniformMatrix4fv(mMatrixHandler, 1, false, mMVPMatrix, 0);

        int mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // 类似于锁
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false,
                0, vertexBuffer);
        // 处理颜色
        int mColorHandle = GLES20.glGetUniformLocation(mProgram, "aColor");
        GLES20.glUniform4fv(mColorHandle,2,colors,0);
        GLES20.glEnableVertexAttribArray(mColorHandle);
        GLES20.glVertexAttribPointer(mColorHandle,4,GLES20.GL_FLOAT,false,0,colorBuffer);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES,index.length,GLES20.GL_UNSIGNED_SHORT,indexBuffer);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

    // 矩阵坐标转换写法
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        float ratio = 1.f * width / height;
        // 投影矩阵，投影的面
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 20);
        // 相机（观察者）
        Matrix.setLookAtM(mViewMatrix, 0, 5.0f, 5.0f, 10.0f, // 相机坐标
                0f, 0f, 0f,  // 目标物坐标
                0f, 1.0f, 1.0f); // 相机观察的方向
        // 计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);

    }
}
