package org.telegram.ui.Components.Paint;

import android.graphics.Color;
import android.opengl.GLES20;
import java.util.HashMap;
import java.util.Map;
import org.telegram.messenger.FileLog;

public class Shader
{
  private int fragmentShader;
  protected int program = GLES20.glCreateProgram();
  protected Map<String, Integer> uniformsMap = new HashMap();
  private int vertexShader;
  
  public Shader(String paramString1, String paramString2, String[] paramArrayOfString1, String[] paramArrayOfString2)
  {
    paramString1 = compileShader(35633, paramString1);
    if (paramString1.status == 0)
    {
      FileLog.e("tmessages", "Vertex shader compilation failed");
      destroyShader(paramString1.shader, 0, this.program);
    }
    do
    {
      return;
      paramString2 = compileShader(35632, paramString2);
      if (paramString2.status == 0)
      {
        FileLog.e("tmessages", "Fragment shader compilation failed");
        destroyShader(paramString1.shader, paramString2.shader, this.program);
        return;
      }
      GLES20.glAttachShader(this.program, paramString1.shader);
      GLES20.glAttachShader(this.program, paramString2.shader);
      int i = 0;
      while (i < paramArrayOfString1.length)
      {
        GLES20.glBindAttribLocation(this.program, i, paramArrayOfString1[i]);
        i += 1;
      }
      if (linkProgram(this.program) == 0)
      {
        destroyShader(paramString1.shader, paramString2.shader, this.program);
        return;
      }
      int k = paramArrayOfString2.length;
      i = j;
      while (i < k)
      {
        paramArrayOfString1 = paramArrayOfString2[i];
        this.uniformsMap.put(paramArrayOfString1, Integer.valueOf(GLES20.glGetUniformLocation(this.program, paramArrayOfString1)));
        i += 1;
      }
      if (paramString1.shader != 0) {
        GLES20.glDeleteShader(paramString1.shader);
      }
    } while (paramString2.shader == 0);
    GLES20.glDeleteShader(paramString2.shader);
  }
  
  public static void SetColorUniform(int paramInt1, int paramInt2)
  {
    GLES20.glUniform4f(paramInt1, Color.red(paramInt2) / 255.0F, Color.green(paramInt2) / 255.0F, Color.blue(paramInt2) / 255.0F, Color.alpha(paramInt2) / 255.0F);
  }
  
  private CompilationResult compileShader(int paramInt, String paramString)
  {
    paramInt = GLES20.glCreateShader(paramInt);
    GLES20.glShaderSource(paramInt, paramString);
    GLES20.glCompileShader(paramInt);
    paramString = new int[1];
    GLES20.glGetShaderiv(paramInt, 35713, paramString, 0);
    if (paramString[0] == 0) {
      FileLog.e("tmessages", GLES20.glGetShaderInfoLog(paramInt));
    }
    return new CompilationResult(paramInt, paramString[0]);
  }
  
  private void destroyShader(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt1 != 0) {
      GLES20.glDeleteShader(paramInt1);
    }
    if (paramInt2 != 0) {
      GLES20.glDeleteShader(paramInt2);
    }
    if (paramInt3 != 0) {
      GLES20.glDeleteProgram(paramInt1);
    }
  }
  
  private int linkProgram(int paramInt)
  {
    GLES20.glLinkProgram(paramInt);
    int[] arrayOfInt = new int[1];
    GLES20.glGetProgramiv(paramInt, 35714, arrayOfInt, 0);
    if (arrayOfInt[0] == 0) {
      FileLog.e("tmessages", GLES20.glGetProgramInfoLog(paramInt));
    }
    return arrayOfInt[0];
  }
  
  public void cleanResources()
  {
    if (this.program != 0)
    {
      GLES20.glDeleteProgram(this.vertexShader);
      this.program = 0;
    }
  }
  
  public int getUniform(String paramString)
  {
    return ((Integer)this.uniformsMap.get(paramString)).intValue();
  }
  
  private class CompilationResult
  {
    int shader;
    int status;
    
    CompilationResult(int paramInt1, int paramInt2)
    {
      this.shader = paramInt1;
      this.status = paramInt2;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/Paint/Shader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */