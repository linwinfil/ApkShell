#ifdef GL_ES
precision mediump float;
#endif

uniform vec2 u_resolution;
uniform vec2 u_mouse;
uniform float u_time;

void test()
{
    vec3 c;
    float l;
    float t = u_time;
    float z = t;
    vec2 r = u_resolution.xy;
    vec2 st = gl_FragCoord.xy/u_resolution.xy;
    for (int i=0;i<8;i++) {
        vec2 uv, p=st.xy;
        uv=p;
        p-=0.5;
        p.x*=r.x/r.y;
        z+=-0.60;
        l=length(p);
        uv+=p/l*(sin(z)+1.)*abs(sin(l*9.-z*1.648));
        c[i]=0.01/length(abs(mod(uv, 1.)-.5));
    }
    gl_FragColor = vec4(c/l, t);
}   

void test2()
{
    float u_diagonal = 0.848;
    vec4 color = vec4(123);
    vec2 npos = gl_FragCoord.xy / u_resolution.xy;// 0.0 .. 1.0
    float aspect = u_resolution.x / u_resolution.y;// aspect ratio x/y
    vec2 ratio = vec2(aspect, 1.0);// aspect ratio (x/y,1)
    vec2 uv = (npos * 2.0 - 1.0) * ratio;// -1 .. -1
    float r1 = (1.0 - (1.0 - u_diagonal)) / (aspect * 2.0);
//    float r2 = 1.0f;
//    if (uv.y > 0.0){
//        r2 = (abs(uv.y) - u_diagonal) / (aspect + uv.x);
//    } else {
//        r2 = (abs(uv.y) - u_diagonal) / (aspect - uv.x);
//    }
    float r2 = (abs(uv.y)- (1.0 - u_diagonal)) / (aspect + uv.x * (-1.0 + 2.0 * step(0.0,uv.y)));
    float alpha = step(r2, r1);
    vec4 bk = vec4(0.0, 0.0, 0.0, 0.0);
    gl_FragColor = mix(bk, color, alpha);

}

void main()
{
    test2();
//     vec2 st = gl_FragCoord.xy/u_resolution.xy;
//   	vec2 uv = st.xy - vec2(0.5);
    
//     float uRadius = 0.1;
//     float uRatio = 1.0;
//     vec4 color = vec4(1.0);
    
//   	float radiusOffset = 0.5 - uRadius;//最短边半径
//   	float radiusYOffset = radiusOffset * uRatio;//完整图像宽高
    
//   	float rx = mod(abs(uv.x), radiusOffset);
//   	float ry = mod(abs(uv.y), radiusYOffset) / uRatio;
//   	float mx = (radiusOffset >= abs(uv.x) ? 0.0 : 1.0);
//   	float my = (radiusYOffset >= abs(uv.y) ? 0.0 : 1.0);
//   	float mr = (uRadius >= length(vec2(rx, ry)) ? 0.0 : 1.0);
//   	float alpha = 1.0 - mx * my * mr;
//   	if (alpha < 1.0 || (abs(uv.x) > 0.5) || (abs(uv.y) > 0.5 * uRatio)) {
//   		color = vec4(0.0, 0.0, 0.0, 0.0);
//   	}
//   	gl_FragColor = color;  
    
}
