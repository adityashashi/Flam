#version 300 es
layout(location = 0) in vec2 aPosition;
layout(location = 1) in vec2 aVelocity;
layout(location = 2) in float aStartTime;
layout(location = 3) in vec4 aColor;

uniform float uTime;
uniform vec2 uGravity;

out vec4 vColor;

void main() {
    float dt = uTime - aStartTime;
    vec2 position = aPosition + aVelocity * dt + 0.5 * uGravity * dt * dt;
    gl_Position = vec4(position, 0.0, 1.0);
    gl_PointSize = 10.0;
    float life = max(1.0 - dt, 0.0);
    vColor = vec4(aColor.rgb, life);
}
