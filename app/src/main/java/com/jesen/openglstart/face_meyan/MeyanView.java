package com.jesen.openglstart.face_meyan;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class MeyanView extends GLSurfaceView {
    public MeyanView(Context context) {
        super(context);
    }

    public MeyanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
        setRenderer(new MeyanRender(this));

        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }
}
