package com.example.fireworks;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class FireworksRenderer implements GLSurfaceView.Renderer {
    private final Context context;
    private final List<Particle> particles = new ArrayList<>();
    private final Random random = new Random();

    private int program;
    private int positionHandle;
    private int colorHandle;
    private int mvpMatrixHandle;
    private int[] vbo = new int[1];

    private final float[] projectionMatrix = new float[16];

    public FireworksRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(javax.microedition.khronos.egl.EGLConfig config) {
        GLES30.glClearColor(0f, 0f, 0f, 1f);
        GLES30.glEnable(GLES30.GL_BLEND);
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE);
        GLES30.glEnable(GLES30.GL_PROGRAM_POINT_SIZE);
        GLES30.glEnable(GLES30.GL_POINT_SPRITE); // Optional depending on driver

        String vertexShaderCode = ShaderUtils.loadShaderFromAssets(context, "vertex_shader.glsl");
        String fragmentShaderCode = ShaderUtils.loadShaderFromAssets(context, "fragment_shader.glsl");

        int vertexShader = ShaderUtils.compileShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = ShaderUtils.compileShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode);

        program = ShaderUtils.linkProgram(vertexShader, fragmentShader);
        GLES30.glUseProgram(program);

        positionHandle = GLES30.glGetAttribLocation(program, "a_Position");
        colorHandle = GLES30.glGetAttribLocation(program, "a_Color");
        mvpMatrixHandle = GLES30.glGetUniformLocation(program, "u_MVPMatrix");

        GLES30.glGenBuffers(1, vbo, 0);
    }

    @Override
    public void onSurfaceChanged(javax.microedition.khronos.egl.EGLConfig config, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
        Matrix.orthoM(projectionMatrix, 0, -width / 2, width / 2, -height / 2, height / 2, -1, 1);
    }

    @Override
    public void onDrawFrame(javax.microedition.khronos.opengles.GL10 gl) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        GLES30.glUseProgram(program);

        long currentTime = System.currentTimeMillis();
        Iterator<Particle> iterator = particles.iterator();
        while (iterator.hasNext()) {
            Particle p = iterator.next();
            if (currentTime - p.startTime > p.lifetime) {
                iterator.remove();
            } else {
                float lifeRatio = (currentTime - p.startTime) / (float) p.lifetime;
                p.alpha = 1.0f - lifeRatio;
                p.position[0] += p.velocity[0];
                p.position[1] += p.velocity[1];
            }
        }

        FloatBuffer buffer = ByteBuffer.allocateDirect(particles.size() * 7 * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        for (Particle p : particles) {
            buffer.put(p.position[0]);
            buffer.put(p.position[1]);
            buffer.put(0f);
            buffer.put(p.color[0]);
            buffer.put(p.color[1]);
            buffer.put(p.color[2]);
            buffer.put(p.alpha);
        }
        buffer.position(0);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbo[0]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, buffer.capacity() * 4, buffer, GLES30.GL_DYNAMIC_DRAW);

        GLES30.glEnableVertexAttribArray(positionHandle);
        GLES30.glVertexAttribPointer(positionHandle, 3, GLES30.GL_FLOAT, false, 7 * 4, 0);

        GLES30.glEnableVertexAttribArray(colorHandle);
        GLES30.glVertexAttribPointer(colorHandle, 4, GLES30.GL_FLOAT, false, 7 * 4, 3 * 4);

        GLES30.glUniformMatrix4fv(mvpMatrixHandle, 1, false, projectionMatrix, 0);
        GLES30.glDrawArrays(GLES30.GL_POINTS, 0, particles.size());

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
    }

    public void triggerBurst(float x, float y) {
        for (int i = 0; i < 300; i++) {
            float angle = random.nextFloat() * 360;
            float speed = random.nextFloat() * 6f + 2f;
            float dx = (float) Math.cos(Math.toRadians(angle)) * speed;
            float dy = (float) Math.sin(Math.toRadians(angle)) * speed;

            float[] color = new float[]{random.nextFloat(), random.nextFloat(), random.nextFloat()};
            particles.add(new Particle(new float[]{x, y}, new float[]{dx, dy}, color, 1500));
        }
    }
}
