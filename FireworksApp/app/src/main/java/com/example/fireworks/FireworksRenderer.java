package com.example.fireworks;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.View;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGLConfig;

public class FireworksRenderer implements GLSurfaceView.Renderer, View.OnTouchListener {

    private static final int MAX_PARTICLES = 1000;
    private static final float[] GRAVITY = {0.0f, -0.5f};

    private final Context context;
    private final Particle[] particles = new Particle[MAX_PARTICLES];
    private final FloatBuffer vertexBuffer;

    private int program;
    private int uTimeLocation;
    private int uGravityLocation;
    private int aPositionLocation;
    private int aVelocityLocation;
    private int aStartTimeLocation;
    private int aColorLocation;

    private long startTime;

    public FireworksRenderer(Context context) {
        this.context = context;

        // Initialize particles
        for (int i = 0; i < MAX_PARTICLES; i++) {
            particles[i] = new Particle();
        }

        // Allocate buffer
        vertexBuffer = ByteBuffer.allocateDirect(MAX_PARTICLES * Particle.BYTES)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES30.glClearColor(0f, 0f, 0f, 1f);
        GLES30.glEnable(GLES30.GL_BLEND);
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE);

        String vertexShaderCode = ShaderUtils.readShaderFileFromRawResource(context, R.raw.vertex_shader);
        String fragmentShaderCode = ShaderUtils.readShaderFileFromRawResource(context, R.raw.fragment_shader);

        int vertexShader = ShaderUtils.compileShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = ShaderUtils.compileShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode);

        program = ShaderUtils.linkProgram(vertexShader, fragmentShader);
        GLES30.glUseProgram(program);

        // Get attribute and uniform locations
        aPositionLocation = GLES30.glGetAttribLocation(program, "aPosition");
        aVelocityLocation = GLES30.glGetAttribLocation(program, "aVelocity");
        aStartTimeLocation = GLES30.glGetAttribLocation(program, "aStartTime");
        aColorLocation = GLES30.glGetAttribLocation(program, "aColor");

        uTimeLocation = GLES30.glGetUniformLocation(program, "uTime");
        uGravityLocation = GLES30.glGetUniformLocation(program, "uGravity");

        startTime = System.currentTimeMillis();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);

        float currentTime = (System.currentTimeMillis() - startTime) / 1000f;
        GLES30.glUniform1f(uTimeLocation, currentTime);
        GLES30.glUniform2f(uGravityLocation, GRAVITY[0], GRAVITY[1]);

        vertexBuffer.position(0);
        for (Particle particle : particles) {
            particle.updateBuffer(vertexBuffer);
        }
        vertexBuffer.position(0);

        GLES30.glEnableVertexAttribArray(aPositionLocation);
        GLES30.glEnableVertexAttribArray(aVelocityLocation);
        GLES30.glEnableVertexAttribArray(aStartTimeLocation);
        GLES30.glEnableVertexAttribArray(aColorLocation);

        int stride = Particle.BYTES;
        vertexBuffer.position(0);
        GLES30.glVertexAttribPointer(aPositionLocation, 2, GLES30.GL_FLOAT, false, stride, vertexBuffer);

        vertexBuffer.position(2);
        GLES30.glVertexAttribPointer(aVelocityLocation, 2, GLES30.GL_FLOAT, false, stride, vertexBuffer);

        vertexBuffer.position(4);
        GLES30.glVertexAttribPointer(aStartTimeLocation, 1, GLES30.GL_FLOAT, false, stride, vertexBuffer);

        vertexBuffer.position(5);
        GLES30.glVertexAttribPointer(aColorLocation, 4, GLES30.GL_FLOAT, false, stride, vertexBuffer);

        GLES30.glDrawArrays(GLES30.GL_POINTS, 0, MAX_PARTICLES);

        GLES30.glDisableVertexAttribArray(aPositionLocation);
        GLES30.glDisableVertexAttribArray(aVelocityLocation);
        GLES30.glDisableVertexAttribArray(aStartTimeLocation);
        GLES30.glDisableVertexAttribArray(aColorLocation);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            spawnParticles(event.getX(), event.getY());
            return true;
        }
        return false;
    }

    private void spawnParticles(float x, float y) {
        Random random = new Random();
        float currentTime = (System.currentTimeMillis() - startTime) / 1000f;

        for (Particle particle : particles) {
            float angle = (float) (random.nextFloat() * 2 * Math.PI);
            float speed = random.nextFloat() * 0.5f + 0.5f;
            float vx = (float) Math.cos(angle) * speed;
            float vy = (float) Math.sin(angle) * speed;

            float r = random.nextFloat();
            float g = random.nextFloat();
            float b = random.nextFloat();

            particle.set(x, y, vx, vy, currentTime, r, g, b, 1.0f);
        }
    }
}
