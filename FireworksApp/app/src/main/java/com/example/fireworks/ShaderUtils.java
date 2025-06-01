package com.example.fireworks;

import android.content.Context;
import android.opengl.GLES30;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ShaderUtils {

    public static String readShaderFileFromRawResource(Context context, int resourceId) {
        StringBuilder shaderCode = new StringBuilder();
        try {
            InputStream inputStream = context.getResources().openRawResource(resourceId);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                shaderCode.append(line).append('\n');
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return shaderCode.toString();
    }

    public static int compileShader(int type, String shaderCode) {
        int shader = GLES30.glCreateShader(type);
        GLES30.glShaderSource(shader, shaderCode);
        GLES30.glCompileShader(shader);

        int[] compileStatus = new int[1];
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compileStatus, 
::contentReference[oaicite:0]{index=0}
 
