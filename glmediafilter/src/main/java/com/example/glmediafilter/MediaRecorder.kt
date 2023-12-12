package com.example.glmediafilter

import android.content.Context
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.opengl.EGLContext
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface
import com.example.glmediafilter.render.EGLEnv
import java.io.IOException
/**
 * 取FBO数据编码存储
 */
class MediaRecorder(
    context: Context,
    private val filePath: String,
    private val eglContext: EGLContext,
    private val width: Int,
    private val height: Int
) {
    private var mMediaCodec: MediaCodec? = null
    private var mSurface: Surface? = null
    private var mMuxer: MediaMuxer? = null
    private var mHandler: Handler? = null
    private var eGLEnv: EGLEnv? = null
    private var isStart = false
    private val tag = "MediaRecorder"
    private var mLastTimeStamp: Long = 0
    private var mediaTrack = 0
    private var mSpeed = 0.1f
    private val mContext = context.applicationContext

    fun start(speed: Float) {
        mSpeed = speed
        val mediaFormat =
            MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height)
        with(mediaFormat) {
            // 色彩空间
            setInteger(
                MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
            )
            setInteger(MediaFormat.KEY_FRAME_RATE, 25)      // 帧率
            setInteger(MediaFormat.KEY_BIT_RATE, 1500_000) // 码率
            setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 10) // 关键帧间隔
        }
        try {
            mMediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        mMediaCodec?.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)

        mSurface = mMediaCodec?.createInputSurface()

        mMuxer = MediaMuxer(filePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

        mMediaCodec?.start()

        val handlerThread = HandlerThread("mediacodec-gl")
        handlerThread.start()

        Log.d(tag, "---init-thread:${Thread.currentThread().name}")

        mHandler = Handler(handlerThread.looper)
        mHandler!!.post {
            eGLEnv = EGLEnv(mContext, eglContext, mSurface!!, width, height)
            isStart = true
        }
    }

    /**
     * 通过textureId获取FBO数据并编码
     */
    fun fireFrame(textureId: Int, timestamp: Long) {
        if (!isStart) {
            return
        }
        mHandler?.post {
            Log.d(tag, "---fireFrame-thread:${Thread.currentThread().name}")
            eGLEnv?.draw(textureId, timestamp)
            enCodec(false)
        }
    }

    private fun enCodec(endOfStream: Boolean) {
        if (mMediaCodec == null) return
        if (endOfStream){
            // 编码完毕
            mMediaCodec?.signalEndOfInputStream()
        }
        while (true) {
            val bufferInfo = MediaCodec.BufferInfo()
            val index = mMediaCodec!!.dequeueOutputBuffer(bufferInfo, 10_000)
            if (index == MediaCodec.INFO_TRY_AGAIN_LATER) {
                //如果是结束那直接退出，否则继续循环
                if (!endOfStream) {
                    break
                }
            } else if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                //输出格式发生改变  第一次总会调用所以在这里开启混合器
                val newFormat = mMediaCodec!!.outputFormat
                mediaTrack = mMuxer!!.addTrack(newFormat)
                mMuxer!!.start()
            } else if (index == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                //可以忽略
            } else {
                //调整时间戳
                bufferInfo.presentationTimeUs = (bufferInfo.presentationTimeUs / mSpeed).toLong()
                //有时候会出现异常 ： timestampUs xxx < lastTimestampUs yyy for Video track
                if (bufferInfo.presentationTimeUs <= mLastTimeStamp) {
                    bufferInfo.presentationTimeUs =
                        ((mLastTimeStamp + 1000_000 / 25 / mSpeed).toLong())
                }
                mLastTimeStamp = bufferInfo.presentationTimeUs

                //正常则 index 获得缓冲区下标
                val encodedData = mMediaCodec!!.getOutputBuffer(index)
                //如果当前的buffer是配置信息，不管它 不用写出去
                if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG != 0) {
                    bufferInfo.size = 0
                }
                if (bufferInfo.size != 0) {
                    encodedData?.let { data ->
                        //设置从哪里开始读数据(读出来就是编码后的数据)
                        data.position(bufferInfo.offset)
                        //设置能读数据的总长度
                        data.limit(bufferInfo.offset + bufferInfo.size)
                        //写出为mp4
                        mMuxer!!.writeSampleData(mediaTrack, data, bufferInfo)
                    }
                }
                // 释放这个缓冲区，后续可以存放新的编码后的数据啦
                mMediaCodec!!.releaseOutputBuffer(index, false)
                // 如果给了结束信号 signalEndOfInputStream
                if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                    break
                }
            }
        }
    }

    fun stop() {
        // 释放
        isStart = false
        mHandler?.post {
            enCodec(true)
            mMediaCodec?.stop()
            mMediaCodec?.release()
            mMuxer?.stop()
            mMuxer?.release()
            eGLEnv?.release()
            mHandler?.looper?.quitSafely()
            mMediaCodec = null
            eGLEnv = null
            mMuxer = null
            mSurface = null
            mHandler = null
        }
    }
}