#version 300 es
precision mediump float;
const int   c_samplesX    = 9;// must be odd
const int   c_samplesY    = 9;// must be odd
const float c_textureSize = 512.0;
const int   c_halfSamplesX = c_samplesX / 2;
const int   c_halfSamplesY = c_samplesY / 2;
const float c_pixelSize = (1.0 / c_textureSize);

in vec2 TexCoords;
out vec4 gl_FragColor;

layout (location = 3) uniform mediump float GaussianSigma;
layout (location = 4) uniform sampler2D vTexture;

float Gaussian (float sigma, float x)
{
    return exp(-(x*x) / (2.0 * sigma*sigma+1.));
}

vec3 BlurredPixel (vec2 uv)
{
    float c_sigmaX      = GaussianSigma;
    float c_sigmaY      = c_sigmaX;

    float total = 0.0;
    vec3 ret = vec3(0.);

    for (int iy = 1; iy <= c_samplesY; ++iy)
    {
        float fy = Gaussian(c_sigmaY, float(iy) - float(c_halfSamplesY));
        float offsety = float(iy-c_halfSamplesY) * c_pixelSize;
        for (int ix = 1; ix <= c_samplesX; ++ix)
        {
            float fx = Gaussian(c_sigmaX, float(ix) - float(c_halfSamplesX));
            float offsetx = float(ix-c_halfSamplesX) * c_pixelSize;
            total += fx * fy;
            ret += texture(vTexture, uv + vec2(offsetx, offsety)).rgb * fx*fy;
        }
    }
    return ret / total;
}

void main()
{
    vec2 uv = TexCoords;
    if (GaussianSigma == 0.) {
        gl_FragColor = texture(vTexture, uv);
    } else {
        gl_FragColor = vec4(BlurredPixel(uv), 1.0);
    }
}