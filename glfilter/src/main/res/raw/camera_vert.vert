// 顶点坐标
attribute vec4 vPosition;
// 纹理坐标
attribute vec4 vCoord;

uniform vec4 vMatrix;

// 像素坐标,在顶点着色器中没有实际意义，主要用于片元
varying vec2 aCoord;

void main() {
    // 告诉gpu渲染的形状
    gl_Position = vPosition;
    aCoord = (vMatrix * vCoord).xy;
}