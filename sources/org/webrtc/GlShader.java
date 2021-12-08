package org.webrtc;

import android.opengl.GLES20;
import java.nio.FloatBuffer;
import org.telegram.messenger.FileLog;

public class GlShader {
    private static final String TAG = "GlShader";
    private int program;

    private static int compileShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compileStatus = {0};
            GLES20.glGetShaderiv(shader, 35713, compileStatus, 0);
            if (compileStatus[0] == 1) {
                GlUtil.checkNoGLES2Error("compileShader");
                return shader;
            }
            Logging.e("GlShader", "Compile error " + GLES20.glGetShaderInfoLog(shader) + " in shader:\n" + source);
            throw new RuntimeException(GLES20.glGetShaderInfoLog(shader));
        }
        throw new RuntimeException("glCreateShader() failed. GLES20 error: " + GLES20.glGetError());
    }

    public GlShader(String vertexSource, String fragmentSource) {
        int vertexShader = compileShader(35633, vertexSource);
        int fragmentShader = compileShader(35632, fragmentSource);
        int glCreateProgram = GLES20.glCreateProgram();
        this.program = glCreateProgram;
        if (glCreateProgram != 0) {
            GLES20.glAttachShader(glCreateProgram, vertexShader);
            GLES20.glAttachShader(this.program, fragmentShader);
            GLES20.glLinkProgram(this.program);
            int[] linkStatus = {0};
            GLES20.glGetProgramiv(this.program, 35714, linkStatus, 0);
            if (linkStatus[0] != 1) {
                Logging.e("GlShader", "Could not link program: " + GLES20.glGetProgramInfoLog(this.program));
                throw new RuntimeException(GLES20.glGetProgramInfoLog(this.program));
            }
            return;
        }
        throw new RuntimeException("glCreateProgram() failed. GLES20 error: " + GLES20.glGetError());
    }

    public int getAttribLocation(String label) {
        int i = this.program;
        if (i != -1) {
            int location = GLES20.glGetAttribLocation(i, label);
            if (location >= 0) {
                return location;
            }
            throw new RuntimeException("Could not locate '" + label + "' in program");
        }
        throw new RuntimeException("The program has been released");
    }

    public void setVertexAttribArray(String label, int dimension, FloatBuffer buffer) {
        setVertexAttribArray(label, dimension, 0, buffer);
    }

    public void setVertexAttribArray(String label, int dimension, int stride, FloatBuffer buffer) {
        if (this.program != -1) {
            int location = getAttribLocation(label);
            GLES20.glEnableVertexAttribArray(location);
            GLES20.glVertexAttribPointer(location, dimension, 5126, false, stride, buffer);
            GlUtil.checkNoGLES2Error("setVertexAttribArray");
            return;
        }
        throw new RuntimeException("The program has been released");
    }

    public int getUniformLocation(String label) {
        int i = this.program;
        if (i != -1) {
            int location = GLES20.glGetUniformLocation(i, label);
            if (location >= 0) {
                return location;
            }
            throw new RuntimeException("Could not locate uniform '" + label + "' in program");
        }
        throw new RuntimeException("The program has been released");
    }

    public void useProgram() {
        if (this.program != -1) {
            synchronized (EglBase.lock) {
                GLES20.glUseProgram(this.program);
            }
            try {
                GlUtil.checkNoGLES2Error("glUseProgram");
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else {
            throw new RuntimeException("The program has been released");
        }
    }

    public void release() {
        Logging.d("GlShader", "Deleting shader.");
        int i = this.program;
        if (i != -1) {
            GLES20.glDeleteProgram(i);
            this.program = -1;
        }
    }
}
