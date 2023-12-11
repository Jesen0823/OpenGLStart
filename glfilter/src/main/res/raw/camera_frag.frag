// 声明采样器
#extension GL_OES_EGL_image_external: require

// 所有float类型数据精度是lowp
precision mediump float;

// 坐标，顶点着色器传递过来
varying vec2 aCoord;

// 采样器 uniform表示static
uniform samplerExternalOES vTexture;

void main() {
    // 将坐标aCoord的xy对应的颜色值采样出来
    vec4 rgba = texture2D(vTexture, aCoord);
    // 转为片元颜色
    gl_FragColor = vec4(rgba.r, rgba.g, rgba.b, rgba.a);

}