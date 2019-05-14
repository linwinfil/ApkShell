precision mediump float;
uniform sampler2D vTexture;
uniform float vProgress;
uniform int vType;
varying vec2 aCoordinate;

void main(){
    //2D纹理采样 纹理 + 纹理坐标
    vec4 aColor = texture2D(vTexture, aCoordinate);
    float r = aColor.r * abs(sin(1.0 * vProgress / 100.0));
    float g = aColor.g * abs(cos(1.0 * vProgress / 100.0));
    float b = aColor.b * abs(tan(1.0 * vProgress / 100.0));
    vec4 c = vec4(r, g, b, aColor.a);
    gl_FragColor= c;
}