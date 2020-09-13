#version 300 es
precision highp float;

in vec2 TexCoords;
out vec4 FragColor;

layout (location = 3) uniform sampler2D inputImageTexture;// 图像texture
layout (location = 4) uniform sampler2D curveTexture;// 滤镜texture
layout (location = 5) uniform lowp float intensity;// 0 ~ 1.0f

void main() {
    lowp vec4 textureColor = texture(inputImageTexture, TexCoords);

    //1、用蓝色值计算正方形的位置，得到quad1和quad2
    //2、根据红色和绿色值就算对应位置在整个纹理的坐标，得到texPos1和texPos2
    //3、根据texPos1和texPos2读取映射结果，再用蓝色值的小部分进行mix操作

    //获取B分量值，确定LUT小方格的index，取之范围为0-15
    mediump float blueColor = textureColor.b * 15.0;

    //取与B分量值最接近的2个小方格坐标
    mediump vec2 quad1;
    quad1.y = floor(blueColor / 4.0);
    quad1.x = floor(blueColor) - (quad1.y * 4.0);

    mediump vec2 quad2;
    quad2.y = floor(ceil(blueColor) / 4.0);
    quad2.x = ceil(blueColor) - (quad2.y * 4.0);

    //通过R和G分量的值确定小方格内目标映射的RGB组合的坐标，然后归一化转化为纹理坐标
    highp vec2 texPos1;
    texPos1.x = (quad1.x * 0.25) + 0.5 / 64.0 + ((0.25 - 1.0 / 64.0) * textureColor.r);
    texPos1.y = (quad1.y * 0.25) + 0.5 / 64.0 + ((0.25 - 1.0 / 64.0) * textureColor.g);

    highp vec2 texPos2;
    texPos2.x = (quad2.x * 0.25) + 0.5 / 64.0 + ((0.25 - 1.0 / 64.0) * textureColor.r);
    texPos2.y = (quad2.y * 0.25) + 0.5 / 64.0 + ((0.25 - 1.0 / 64.0) * textureColor.g);

    //取目标映射对应的像素值
    lowp vec4 newColor1 = texture(curveTexture, texPos1);
    lowp vec4 newColor2 = texture(curveTexture, texPos2);

    //使用Mix方法对2个边界像素值进行混合
    lowp vec4 newColor = mix(newColor1, newColor2, fract(blueColor));
    FragColor = mix(textureColor, vec4(newColor.rgb, textureColor.w), intensity);
}