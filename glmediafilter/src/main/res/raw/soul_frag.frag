// 上色点的坐标
varying highp vec2 aCoord;
// 采样器
uniform sampler2D vTexture;
// scalePercent>1
uniform highp float scalePercent;
// 透明度混合，由大变小
uniform lowp float mixturePercent;

void main() {
    // [rgba]
    lowp vec4 textureColor = texture2D(vTexture, aCoord);

    highp vec2 center = vec2(0.5, 0.5);
    // 临时变量
    highp vec2 textureCoordinateToUse = aCoord;
    textureCoordinateToUse -= center;
    textureCoordinateToUse = textureCoordinateToUse / scalePercent;
    textureCoordinateToUse += center;
    // 新采样的颜色
    lowp vec4 textureColor2 = texture2D(vTexture, textureCoordinateToUse);

    gl_FragColor = mix(textureColor, textureColor2, mixturePercent);

    // 用采样器采样坐标,在此之前做特效处理
    //gl_FragColor = texture2D(vTexture, textureCoordinateToUse);
}