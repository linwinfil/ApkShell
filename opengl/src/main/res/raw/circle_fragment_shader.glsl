precision mediump float;
uniform sampler2D uTexture;
uniform float uRadius;
uniform float uRatio;
varying vec2 textureCoord;
void main() {
    float radiusOffset = 0.5 - uRadius;//半径
    float radiusYOffset = radiusOffset * uRatio;//完整图像宽高
    vec2 uv = textureCoord.xy - vec2(0.5, 0.5);
    float rx = mod(abs(uv.x), radiusOffset);
    float ry = mod(abs(uv.y), radiusYOffset) / uRatio;
    // float mx = step(radiusOffset, abs(uv.x));
    // float my = step(radiusYOffset, abs(uv.y));
    // float mr = step(uRadius, length(vec2(rx, ry)));
    float mx = (radiusOffset >= abs(uv.x) ? 0.0 : 1.0);
    float my = (radiusYOffset >= abs(uv.y) ? 0.0 : 1.0);
    float mr = (uRadius >= length(vec2(rx, ry)) ? 0.0 : 1.0);
    float alpha = 1.0 - mx * my * mr;
    vec4 color = texture2D(uTexture, textureCoord);
    if (alpha < 1.0 || (abs(uv.x) > 0.5) || (abs(uv.y) > 0.5 * uRatio)) {
        color = vec4(0.0, 0.0, 0.0, 0.0);
    }

    //if (alpha < 1.0 || (uRadius > 0.0 && ((abs(uv.x) > 0.5) || ((abs(uv.y) / uv.y) * (((abs(uv.y) / uv.y) * 0.5 * yRatio) + uYTrans - uv.y) < 0.0)))) {
    //
    //}
    gl_FragColor = color;
}
