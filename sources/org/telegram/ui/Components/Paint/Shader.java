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

    private class CompilationResult {
        int shader;
        int status;

        CompilationResult(int i, int i2) {
            this.shader = i;
            this.status = i2;
        }
    }

    public Shader(String str, String str2, String[] strArr, String[] strArr2) {
        str = compileShader(35633, str);
        int i = 0;
        if (str.status == 0) {
            if (BuildVars.LOGS_ENABLED != null) {
                FileLog.m1e("Vertex shader compilation failed");
            }
            destroyShader(str.shader, 0, this.program);
            return;
        }
        str2 = compileShader(35632, str2);
        if (str2.status == 0) {
            if (BuildVars.LOGS_ENABLED != null) {
                FileLog.m1e("Fragment shader compilation failed");
            }
            destroyShader(str.shader, str2.shader, this.program);
            return;
        }
        GLES20.glAttachShader(this.program, str.shader);
        GLES20.glAttachShader(this.program, str2.shader);
        for (int i2 = 0; i2 < strArr.length; i2++) {
            GLES20.glBindAttribLocation(this.program, i2, strArr[i2]);
        }
        if (linkProgram(this.program) == null) {
            destroyShader(str.shader, str2.shader, this.program);
            return;
        }
        strArr = strArr2.length;
        while (i < strArr) {
            String str3 = strArr2[i];
            this.uniformsMap.put(str3, Integer.valueOf(GLES20.glGetUniformLocation(this.program, str3)));
            i++;
        }
        if (str.shader != null) {
            GLES20.glDeleteShader(str.shader);
        }
        if (str2.shader != null) {
            GLES20.glDeleteShader(str2.shader);
        }
    }

    public void cleanResources() {
        if (this.program != 0) {
            GLES20.glDeleteProgram(this.vertexShader);
            this.program = 0;
        }
    }

    public int getUniform(String str) {
        return ((Integer) this.uniformsMap.get(str)).intValue();
    }

    private CompilationResult compileShader(int i, String str) {
        i = GLES20.glCreateShader(i);
        GLES20.glShaderSource(i, str);
        GLES20.glCompileShader(i);
        str = new int[1];
        GLES20.glGetShaderiv(i, 35713, str, 0);
        if (str[0] == null && BuildVars.LOGS_ENABLED) {
            FileLog.m1e(GLES20.glGetShaderInfoLog(i));
        }
        return new CompilationResult(i, str[0]);
    }

    private int linkProgram(int i) {
        GLES20.glLinkProgram(i);
        int[] iArr = new int[1];
        GLES20.glGetProgramiv(i, 35714, iArr, 0);
        if (iArr[0] == 0 && BuildVars.LOGS_ENABLED) {
            FileLog.m1e(GLES20.glGetProgramInfoLog(i));
        }
        return iArr[0];
    }

    private void destroyShader(int i, int i2, int i3) {
        if (i != 0) {
            GLES20.glDeleteShader(i);
        }
        if (i2 != 0) {
            GLES20.glDeleteShader(i2);
        }
        if (i3 != 0) {
            GLES20.glDeleteProgram(i);
        }
    }

    public static void SetColorUniform(int i, int i2) {
        GLES20.glUniform4f(i, ((float) Color.red(i2)) / 255.0f, ((float) Color.green(i2)) / 255.0f, ((float) Color.blue(i2)) / 255.0f, ((float) Color.alpha(i2)) / NUM);
    }
}
