#version 300 es

//layout  location 作为定位的标示符号
layout (location = 0) in vec2 aPos;
layout (location = 1) in vec2 aTexCoords;
layout (location = 2) uniform mat4 uTextureMatrix;

out vec2 TexCoords;

void main()
{
    TexCoords = aTexCoords;

    //顶点坐标 = 视图矩阵 * 顶点坐标系
    gl_Position = vec4((uTextureMatrix * vec4(aPos, 0.0f, 1.0f)).xy, 0.0, 1.0);
}