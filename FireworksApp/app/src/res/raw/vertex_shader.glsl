#version 300 es
precision mediump float;

in vec4 v_Color;
in vec2 gl_PointCoord;

out vec4 fragColor;

void main() {
    float dist = distance(gl_PointCoord, vec2(0.5));
    if (dist > 0.5) {
        discard;
    }
    fragColor = v_Color;
}
