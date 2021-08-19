#extension GL_OES_EGL_image_external: require

precision mediump float;
//采样点的坐标
varying vec2 aCoord;
//color 输入，OPenGL采样器
uniform samplerExternalOES vTexture;
void main(){
    // 采集摄像头到OpenGL的采集器
    gl_FragColor =texture2D(vTexture,aCoord);
}