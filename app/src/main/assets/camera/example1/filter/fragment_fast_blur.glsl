#version 300 es
precision mediump float;

in vec2 TexCoords;
out vec4 gl_FragColor;

layout (location = 3) uniform sampler2D inputTexture;
layout (location = 4) uniform int uRadius;
layout (location = 5) uniform float uWidthOffset;
layout (location = 6) uniform float uHeightOffset;

void main()
{
    int diameter = 2 * uRadius + 1;
    vec4 sampleTex;
    vec3 col;
    float weightSum = 0.0;
    for (int i = 0; i < diameter; i++) {
        vec2 offset = vec2(float(i - uRadius) * uWidthOffset, float(i - uRadius) * uHeightOffset);
        sampleTex = texture(inputTexture, TexCoords + offset);
        float index = float(i);
        float boxWeight = float(uRadius) + 1.0 - abs(index - float(uRadius));
        col += sampleTex.rgb * boxWeight;
        weightSum += boxWeight;
    }
    gl_FragColor = vec4(col / weightSum, sampleTex.a);
}