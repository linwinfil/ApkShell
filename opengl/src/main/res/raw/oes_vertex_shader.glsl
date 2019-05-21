attribute vec4 vPosition;
attribute vec2 vCoordinate;
uniform mat4 vMatrix;

varying vec2 aCoordinate;

void main(){
    //顶点坐标= 视图矩阵 * 顶点坐标系
    gl_Position=vMatrix*vPosition;
    //纹理坐标，传递给片段着色器
    aCoordinate=vCoordinate;
}