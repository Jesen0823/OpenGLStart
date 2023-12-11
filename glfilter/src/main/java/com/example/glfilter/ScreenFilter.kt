package com.example.glfilter

import android.content.Context
import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.properties.Delegates

class ScreenFilter(context: Context) {
    private var width = 0
    private var height = 0

    // GPU中的对应变量的句柄，来操作他们
    private var vPosition =-1
    private var vCoord = -1
    private var vMatrix = -1
    private var vTexture = -1

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

    // 顶点缓冲区容器
    private val vertexBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(4 * 4 * 2).order(ByteOrder.nativeOrder()).asFloatBuffer()

    // 纹理坐标
    private val textureBuffer: FloatBuffer

    // 变换矩阵
    private lateinit var matrix: FloatArray

    private var program = -1

    init {
        vertexBuffer.clear()
        vertexBuffer.put(VERTEX)

        textureBuffer = ByteBuffer.allocateDirect(4 * 4 * 2).order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        textureBuffer.clear()
        textureBuffer.put(TEXTURE)

        // 读取顶点着色器程序
        val vertexShader = OpenGlUtil.readRawTextFile(context, R.raw.camera_vert)
        // 读取片元着色器程序
        val fragmentShader = OpenGlUtil.readRawTextFile(context, R.raw.camera_frag)
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

    fun setSize(width: Int, height: Int) {
        this.width = width
        this.height = height
    }

    fun setTransformMatrix(mtx: FloatArray) {
        this.matrix = mtx
    }

    fun onDraw(texture: Int) {
        // 向OpenGL报告View的大小
        GLES20.glViewport(0, 0, width, height)

        // 使用着色器程序
        GLES20.glUseProgram(program)

        // 从索引位0的地方读,读出到着色器中的变量中
        vertexBuffer.position(0)
        // 这里的vPosition,实际上是着色器代码中vPosition的索引index;2个顶点个数;false不标准化
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        GLES20.glEnableVertexAttribArray(vPosition)
        textureBuffer.position(0)
        GLES20.glVertexAttribPointer(
            vCoord, 2, GLES20.GL_FLOAT,
            false, 0, textureBuffer
        )
        // CPU传数据到GPU，默认情况下着色器无法读取到这个数据。 需要我们启用一下才可以读取
        GLES20.glEnableVertexAttribArray(vCoord)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        //生成一个2D采样器vTexture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture)
        GLES20.glUniform1i(vTexture, 0)
        GLES20.glUniformMatrix4fv(vMatrix, 1, false, matrix, 0)

        // 通知绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
    }
}