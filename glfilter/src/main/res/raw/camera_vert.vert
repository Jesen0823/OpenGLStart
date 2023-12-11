#version 200
// 顶点坐标
attribute vec4 vPosition;
// 纹理坐标
attribute vec4 vCoord;

uniform vec4 vMatrix;

// 像素坐标
varying vec4 aCoord;

void main() {
    // 告诉gpu渲染的形状
    gl_position = vPosition;
    aCoord = (vMatrix * vCoord).xy;
}