#version 300 es
precision highp float;

in vec2 TexCoords;
out vec4 FragColor;

layout (location = 3) uniform sampler2D inputImageTexture;// 图像texture
layout (location = 4) uniform sampler2D curveTexture;// 滤镜texture
layout (location = 5) uniform lowp float intensity;// 0 ~ 1.0f

void main() {
    lowp vec4 textureColor = texture(inputImageTexture, TexCoords);

    mediump float blueColor = textureColor.b * 15.0;

    mediump vec2 quad1;
    quad1.y = floor(blueColor / 4.0);
    quad1.x = floor(blueColor) - (quad1.y * 4.0);

    mediump vec2 quad2;
    quad2.y = floor(ceil(blueColor) / 4.0);
    quad2.x = ceil(blueColor) - (quad2.y * 4.0);

    highp vec2 texPos1;
    texPos1.x = (quad1.x * 0.25) + 0.5 / 64.0 + ((0.25 - 1.0 / 64.0) * textureColor.r);
    texPos1.y = (quad1.y * 0.25) + 0.5 / 64.0 + ((0.25 - 1.0 / 64.0) * textureColor.g);

    highp vec2 texPos2;
    texPos2.x = (quad2.x * 0.25) + 0.5 / 64.0 + ((0.25 - 1.0 / 64.0) * textureColor.r);
    texPos2.y = (quad2.y * 0.25) + 0.5 / 64.0 + ((0.25 - 1.0 / 64.0) * textureColor.g);

    lowp vec4 newColor1 = texture(curveTexture, texPos1);
    lowp vec4 newColor2 = texture(curveTexture, texPos2);

    lowp vec4 newColor = mix(newColor1, newColor2, fract(blueColor));
    FragColor = mix(textureColor, vec4(newColor.rgb, textureColor.w), intensity);
}