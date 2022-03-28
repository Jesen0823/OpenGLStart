package com.jesen.meiyancamera.filter;

import android.content.Context;

import com.jesen.meiyancamera.R;

/**
 * 作为显示滤镜   CameraFilter已经渲染好的特效
 * */
public class ScreenFilter extends AbstractFilter{

    public ScreenFilter(Context context){
        super(context, R.raw.base_vertex, R.raw.base_frag);
    }

    @Override
    protected void initCoordinate() {

    }
}
