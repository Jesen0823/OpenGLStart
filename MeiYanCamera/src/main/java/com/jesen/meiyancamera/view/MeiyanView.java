package com.jesen.meiyancamera.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class MeiyanView extends GLSurfaceView {

    public MeiyanRenderer renderer;

    public MeiyanView(Context context) {
        super(context);
    }

    public MeiyanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
        renderer = new MeiyanRenderer(this);
        setRenderer(renderer);
        //设置按需渲染 当我们调用 requestRender 请求GLThread 回调一次 onDrawFrame
        // 连续渲染 就是自动的回调onDrawFrame
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }
}
