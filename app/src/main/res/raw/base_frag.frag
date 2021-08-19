//float数据是什么精度的
precision mediump float;
//采样点的坐标
varying vec2 aCoord;
//color 输入，OPenGL采样器
uniform sampler2D vTexture;
void main(){
//    倒 的   正的
    //变量 接收像素值
    // texture2D：用来采集从OpenGl数据的采样器 采集 aCoord的像素
    //赋值给 gl_FragColor 就可以了
    gl_FragColor =texture2D(vTexture,aCoord);
}