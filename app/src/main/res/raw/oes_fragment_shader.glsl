//预处理指令，控制扩展功能，require指明扩展必须，若不支持则抛出异常
#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec2 aCoordinate;
uniform samplerExternalOES vTexture;

void main(){
    // oes纹理 + 纹理坐标
    gl_FragColor=texture2D(vTexture, aCoordinate);
}