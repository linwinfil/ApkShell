#ifdef GL_ES
precision mediump float;
#endif
uniform float aRatio;
uniform float aAlhpa;
uniform int aType;
varying vec2 aCoordinate;

// 正方
vec4 rectangle(vec2 aCoordinate, float aRatio, float aAlhpa)
{
    vec2 st =  vec2(aCoordinate.xy);
    vec3 color = vec3(0.0);
    // bottom-left
    vec2 bl = step(vec2(aRatio / 2.0), st);//小于0.1的边缘返归0
    float pct = bl.x * bl.y;//这里的乘积*相当于逻辑and

    // top-right
    vec2 tr = step(vec2(aRatio / 2.0), 1.0-st);
    pct *= tr.x * tr.y;

    color = vec3(pct * 0.423, pct * -0.040, pct * 0.7);

    return vec4(color, aAlhpa);
}

//虚化的圆形
vec4 blurCircle(vec2 aCoordinate, int aType)
{
    vec2 st = aCoordinate;
    float pct = 0.0;

    if (aType == 1) {
        // b. The LENGTH of the vector from the pixel to the center
        vec2 toCenter = vec2(0.5)-st;
        pct = length(toCenter);
    } else if (aType == 2) {
        // c. The SQUARE ROOT of the vector  from the pixel to the center
        vec2 tC = vec2(0.5)-st;
        pct = sqrt(tC.x*tC.x+tC.y*tC.y);
    } else if (aType == 4) {
        pct = distance(st, vec2(0.4)) + distance(st, vec2(0.6));
    } else if (aType == 5) {
        pct = distance(st, vec2(0.4)) * distance(st, vec2(0.6));
    } else if (aType == 6) {
        pct = min(distance(st, vec2(0.4)), distance(st, vec2(0.6)));
    } else if (aType == 7) {
        pct = max(distance(st, vec2(0.4)), distance(st, vec2(0.6)));
    } else if (aType == 8) {
        pct = pow(distance(st, vec2(0.4)), distance(st, vec2(0.6)));
    } else {
        // a. The DISTANCE from the pixel to the center
        pct = distance(st, vec2(0.5, 0.5));
    }

    return vec4(pct, pct, pct, 0.89);
}

//圆形
vec4 drawcircle(vec2 aCoordinate, float radius)
{
    float circle = 0.0;
    vec2 st = aCoordinate - vec2(0.5);
    circle = 1.0-smoothstep(radius-(radius*0.01), radius+(radius*0.01), dot(st, st) * 5.0);
    vec3 color = vec3(circle);
    return vec4(color, 1.0);
}

void main()
{
    gl_FragColor = rectangle(aCoordinate, aRatio, aAlhpa);
}

