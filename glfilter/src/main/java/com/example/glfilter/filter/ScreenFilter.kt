package com.example.glfilter.filter

import android.content.Context
import android.opengl.GLES11Ext
import android.opengl.GLES20
import com.example.glfilter.OpenGlUtil
import com.example.glfilter.R
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class ScreenFilter(context: Context):BaseFilter {
    private var mWidth = 0
    private var mHeight = 0

    // GPU中的对应变量的句柄，来操作他们
    private var vPosition =-1
    private var vCoord = -1
    private var vMatrix = -1
    private var vTexture = -1

    // 顶点缓冲区容器
    private val vertexBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(4 * 4 * 2).order(ByteOrder.nativeOrder()).asFloatBuffer()
    // 纹理坐标
    private val textureBuffer: FloatBuffer = ByteBuffer.allocateDirect(4 * 4 * 2).order(ByteOrder.nativeOrder())
        .asFloatBuffer()

    // 变换矩阵
    private var matrix: FloatArray = FloatArray(16)

    private var program = -1

    // 定点着色器
    private val VERTEX = floatArrayOf(
        -1.0f, -1.0f,
        1.0f, -1.0f,
        -1.0f, 1.0f,
        1.0f, 1.0f
    )
    private var TEXTURE = floatArrayOf(
        0.0f, 0.0f,
        1.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 1.0f
    )

    init {
        vertexBuffer.clear()
        vertexBuffer.put(VERTEX)

        textureBuffer.clear()
        textureBuffer.put(TEXTURE)

        // 读取顶点着色器程序
        val vertexShader = OpenGlUtil.readRawTextFile(context, R.raw.camera_vert_sh)
        // 读取片元着色器程序
        val fragmentShader = OpenGlUtil.readRawTextFile(context, R.raw.camera_frag3)
        // 编译程序,链接程序
        program = OpenGlUtil.loadProgram(vertexShader, fragmentShader)


        vPosition = GLES20.glGetAttribLocation(program, "vPosition")
        // 接收纹理坐标
        vCoord = GLES20.glGetAttribLocation(program, "vCoord")
        // 采样点坐标
        vTexture = GLES20.glGetUniformLocation(program, "vTexture")
        // 变换矩阵
        vMatrix = GLES20.glGetUniformLocation(program, "vMatrix")

    }

    override fun onDrawFrame(textureId: Int): Int {
        // 1.设置窗口大小
        GLES20.glViewport(0, 0, mWidth, mHeight)
        // 2.使用着色器程序
        GLES20.glUseProgram(program)

        // 3./ 从索引位0的地方读,读出到着色器中的变量中
        // 3.1 给顶点坐标数据传值
        vertexBuffer.position(0)
        // 这里的vPosition,实际上是着色器代码中vPosition的索引index;2个顶点个数;false不标准化
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        // 激活
        GLES20.glEnableVertexAttribArray(vPosition)

        // 3.2 给纹理坐标数据传值
        textureBuffer.position(0)
        GLES20.glVertexAttribPointer(vCoord, 2, GLES20.GL_FLOAT, false, 0, textureBuffer)
        GLES20.glEnableVertexAttribArray(vCoord)

        // 3.3 变化矩阵传值
        GLES20.glUniformMatrix4fv(vMatrix, 1, false, matrix, 0)

        // 3.4 给片元着色器中的 采样器绑定
        // 激活图层
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        // 图像数据
        GLES20.glBindTexture(GLES11Ext.GL_SAMPLER_EXTERNAL_OES, textureId)
        // 传递参数
        GLES20.glUniform1i(vTexture, 0)

        //参数传递完毕,通知 opengl开始绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        // 解绑
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        return textureId
    }

    override fun setTransformMatrix(mtx: FloatArray) {
        matrix=mtx
    }

    override fun onReady(width: Int, height: Int) {
        mWidth=width
        mHeight=height
    }

    override fun release(){
        GLES20.glDeleteProgram(program)
    }
}