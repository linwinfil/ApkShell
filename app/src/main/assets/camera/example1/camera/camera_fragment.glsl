#version 300 es
#extension GL_OES_EGL_image_external_essl3 : require
precision mediump float;

out vec4 FragColor;

//vertex中的纹理坐标
in vec2 TexCoords;

//外部oes纹理
uniform samplerExternalOES screenTexture;

void main()
{
    vec3 col = texture(screenTexture, TexCoords).rgb;
    FragColor = vec4(col, 1.0f);
}