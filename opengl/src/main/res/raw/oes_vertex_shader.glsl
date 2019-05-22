attribute vec4 vPosition;
attribute vec4 vCoordinate;
uniform mat4 vMatrix;
uniform mat4 vTexMatrix;

varying vec2 aCoordinate;

void main(){
    //顶点坐标= 视图矩阵 * 顶点坐标系
    gl_Position=vMatrix*vPosition;
    //纹理坐标，传递给片段着色器
    aCoordinate = (vTexMatrix * vCoordinate).xy;
}