package com.example.fireworks;

import java.nio.FloatBuffer;

public class Particle {

    public static final int COMPONENT_COUNT = 2 + 2 + 1 + 4; // position(2) + velocity(2) + startTime(1) + color(4)
    public static final int BYTES = COMPONENT_COUNT * 4;

    private float[] data = new float[COMPONENT_COUNT];

    public void set(float x, float y, float vx, float vy, float startTime, float r, float g, float b, float a) {
        data[0] = x;
        data[1] = y;
        data[2] = vx;
        data[3] = vy;
        data[4] = startTime;
        data[5] = r;
        data[6] = g;
        data[7] = b;
        data[8] = a;
    }

    public void updateBuffer(FloatBuffer buffer) {
        buffer.put(data);
    }
}
