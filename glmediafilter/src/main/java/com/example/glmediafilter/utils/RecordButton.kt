package com.example.glmediafilter.utils

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView


class RecordButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr) {

    private var mListener: OnRecordListener? = null

    fun setOnRecordListener(listener: OnRecordListener) {
        mListener = listener
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (mListener == null) {
            return false
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> mListener!!.onRecordStart()
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> mListener!!.onRecordStop()
        }
        return true
    }

    interface OnRecordListener {
        fun onRecordStart()
        fun onRecordStop()
    }
}