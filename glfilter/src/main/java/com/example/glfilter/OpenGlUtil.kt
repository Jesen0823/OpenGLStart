package com.example.glfilter

import android.content.Context
import android.opengl.GLES20
import java.io.BufferedReader
import java.io.InputStreamReader

object OpenGlUtil {

    fun readRawTextFile(context: Context, rawResId:Int):String{
        val inSm = context.applicationContext.resources.openRawResource(rawResId)
        val br = BufferedReader(InputStreamReader(inSm))
        val sb = StringBuilder()
        br.forEachLine {
            sb.append(it)
            sb.append("\n")
        }
        return sb.toString()
    }

    fun loadProgram(vSource: String,fSource:String):Int {
        val vShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)
        // 加载着色器代码
        GLES20.glShaderSource(vShader,vSource)
        // 编译代码
        GLES20.glCompileShader(vShader)
        // 查看编译是否成功
        val status = IntArray(1)
        GLES20.glGetShaderiv(vShader,GLES20.GL_COMPILE_STATUS,status,0)
        if (status[0] != GLES20.GL_TRUE){
            throw IllegalStateException("load shader:${GLES20.glGetShaderInfoLog(vShader)}")
        }

        val fShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)
        // 加载着色器代码
        GLES20.glShaderSource(fShader,fSource)
        // 编译代码
        GLES20.glCompileShader(fShader)
        // 查看编译是否成功
        GLES20.glGetShaderiv(fShader,GLES20.GL_COMPILE_STATUS,status,0)
        if (status[0] != GLES20.GL_TRUE){
            throw IllegalStateException("load shader:${GLES20.glGetShaderInfoLog(fShader)}")
        }

        // 创建着色器程序
        val program = GLES20.glCreateProgram()
        //绑定顶点和片元
        GLES20.glAttachShader(program, vShader)
        GLES20.glAttachShader(program, fShader)
        //链接着色器程序
        GLES20.glLinkProgram(program)
        //获得状态
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, status, 0)
        check(status[0] == GLES20.GL_TRUE) { "link program:" + GLES20.glGetProgramInfoLog(program) }
        GLES20.glDeleteShader(vShader)
        GLES20.glDeleteShader(fShader)
        return program
    }
}