package com.jesen.openglstart.face_meyan.filter;

import android.content.Context;

import com.jesen.openglstart.R;

/**
 * 显示滤镜,将CameraFilter已经处理好的特效显示出来
 * */
public class ScreenFilter extends AbstractFilter {
    public ScreenFilter(Context context) {
        super(context, R.raw.base_vertex, R.raw.base_frag);
    }

    @Override
    protected void initCoordinate() {

    }
}
