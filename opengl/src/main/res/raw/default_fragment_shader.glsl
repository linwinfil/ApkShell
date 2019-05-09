precision mediump float;
uniform sampler2D vTexture;
uniform int vIsHalf;
varying vec2 aCoordinate;

void main(){
    //2D纹理采样 纹理 + 纹理坐标
    gl_FragColor=texture2D(vTexture, aCoordinate);
}