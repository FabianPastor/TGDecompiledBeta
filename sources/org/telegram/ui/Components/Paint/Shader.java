package org.telegram.ui.Components.Paint;

import android.graphics.Color;
import android.opengl.GLES20;
import java.util.HashMap;
import java.util.Map;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;

public class Shader {
    private int fragmentShader;
    protected int program = GLES20.glCreateProgram();
    protected Map<String, Integer> uniformsMap = new HashMap();
    private int vertexShader;

    public Shader(String vertexShader2, String fragmentShader2, String[] attributes, String[] uniforms) {
        CompilationResult vResult = compileShader(35633, vertexShader2);
        if (vResult.status == 0) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("Vertex shader compilation failed");
            }
            destroyShader(vResult.shader, 0, this.program);
            return;
        }
        CompilationResult fResult = compileShader(35632, fragmentShader2);
        if (fResult.status == 0) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("Fragment shader compilation failed");
            }
            destroyShader(vResult.shader, fResult.shader, this.program);
            return;
        }
        GLES20.glAttachShader(this.program, vResult.shader);
        GLES20.glAttachShader(this.program, fResult.shader);
        for (int i = 0; i < attributes.length; i++) {
            GLES20.glBindAttribLocation(this.program, i, attributes[i]);
        }
        if (linkProgram(this.program) == 0) {
            destroyShader(vResult.shader, fResult.shader, this.program);
            return;
        }
        for (String uniform : uniforms) {
            this.uniformsMap.put(uniform, Integer.valueOf(GLES20.glGetUniformLocation(this.program, uniform)));
        }
        if (vResult.shader != 0) {
            GLES20.glDeleteShader(vResult.shader);
        }
        if (fResult.shader != 0) {
            GLES20.glDeleteShader(fResult.shader);
        }
    }

    public void cleanResources() {
        if (this.program != 0) {
            GLES20.glDeleteProgram(this.vertexShader);
            this.program = 0;
        }
    }

    private static class CompilationResult {
        int shader;
        int status;

        CompilationResult(int shader2, int status2) {
            this.shader = shader2;
            this.status = status2;
        }
    }

    public int getUniform(String key) {
        return this.uniformsMap.get(key).intValue();
    }

    private CompilationResult compileShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader, 35713, compileStatus, 0);
        if (compileStatus[0] == 0 && BuildVars.LOGS_ENABLED) {
            FileLog.e(GLES20.glGetShaderInfoLog(shader));
        }
        return new CompilationResult(shader, compileStatus[0]);
    }

    private int linkProgram(int program2) {
        GLES20.glLinkProgram(program2);
        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(program2, 35714, linkStatus, 0);
        if (linkStatus[0] == 0 && BuildVars.LOGS_ENABLED) {
            FileLog.e(GLES20.glGetProgramInfoLog(program2));
        }
        return linkStatus[0];
    }

    private void destroyShader(int vertexShader2, int fragmentShader2, int program2) {
        if (vertexShader2 != 0) {
            GLES20.glDeleteShader(vertexShader2);
        }
        if (fragmentShader2 != 0) {
            GLES20.glDeleteShader(fragmentShader2);
        }
        if (program2 != 0) {
            GLES20.glDeleteProgram(vertexShader2);
        }
    }

    public static void SetColorUniform(int location, int color) {
        GLES20.glUniform4f(location, ((float) Color.red(color)) / 255.0f, ((float) Color.green(color)) / 255.0f, ((float) Color.blue(color)) / 255.0f, ((float) Color.alpha(color)) / 255.0f);
    }
}
