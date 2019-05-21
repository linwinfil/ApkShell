precision mediump float;
varying vec2 aCoordinate;
uniform samplerExternalOES vTexture;

void main(){
    // oes纹理 + 纹理坐标
    gl_FragColor=texture2D(vTexture, aCoordinate);
}