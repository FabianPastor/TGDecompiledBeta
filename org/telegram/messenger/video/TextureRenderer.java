package org.telegram.messenger.video;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.Matrix;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class TextureRenderer
{
  private static final int FLOAT_SIZE_BYTES = 4;
  private static final String FRAGMENT_SHADER = "#extension GL_OES_EGL_image_external : require\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform samplerExternalOES sTexture;\nvoid main() {\n  gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n";
  private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
  private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 20;
  private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;
  private static final String VERTEX_SHADER = "uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n  gl_Position = uMVPMatrix * aPosition;\n  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n";
  private float[] mMVPMatrix = new float[16];
  private int mProgram;
  private float[] mSTMatrix = new float[16];
  private int mTextureID = 53191;
  private FloatBuffer mTriangleVertices;
  private int maPositionHandle;
  private int maTextureHandle;
  private int muMVPMatrixHandle;
  private int muSTMatrixHandle;
  private int rotationAngle;
  
  public TextureRenderer(int paramInt)
  {
    this.rotationAngle = paramInt;
    float[] arrayOfFloat = new float[20];
    float[] tmp38_37 = arrayOfFloat;
    tmp38_37[0] = -1.0F;
    float[] tmp43_38 = tmp38_37;
    tmp43_38[1] = -1.0F;
    float[] tmp48_43 = tmp43_38;
    tmp48_43[2] = 0.0F;
    float[] tmp52_48 = tmp48_43;
    tmp52_48[3] = 0.0F;
    float[] tmp56_52 = tmp52_48;
    tmp56_52[4] = 0.0F;
    float[] tmp60_56 = tmp56_52;
    tmp60_56[5] = 1.0F;
    float[] tmp64_60 = tmp60_56;
    tmp64_60[6] = -1.0F;
    float[] tmp70_64 = tmp64_60;
    tmp70_64[7] = 0.0F;
    float[] tmp75_70 = tmp70_64;
    tmp75_70[8] = 1.0F;
    float[] tmp80_75 = tmp75_70;
    tmp80_75[9] = 0.0F;
    float[] tmp85_80 = tmp80_75;
    tmp85_80[10] = -1.0F;
    float[] tmp91_85 = tmp85_80;
    tmp91_85[11] = 1.0F;
    float[] tmp96_91 = tmp91_85;
    tmp96_91[12] = 0.0F;
    float[] tmp101_96 = tmp96_91;
    tmp101_96[13] = 0.0F;
    float[] tmp106_101 = tmp101_96;
    tmp106_101[14] = 1.0F;
    float[] tmp111_106 = tmp106_101;
    tmp111_106[15] = 1.0F;
    float[] tmp116_111 = tmp111_106;
    tmp116_111[16] = 1.0F;
    float[] tmp121_116 = tmp116_111;
    tmp121_116[17] = 0.0F;
    float[] tmp126_121 = tmp121_116;
    tmp126_121[18] = 1.0F;
    float[] tmp131_126 = tmp126_121;
    tmp131_126[19] = 1.0F;
    tmp131_126;
    this.mTriangleVertices = ByteBuffer.allocateDirect(arrayOfFloat.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
    this.mTriangleVertices.put(arrayOfFloat).position(0);
    Matrix.setIdentityM(this.mSTMatrix, 0);
  }
  
  private int createProgram(String paramString1, String paramString2)
  {
    int i = loadShader(35633, paramString1);
    if (i == 0) {
      i = 0;
    }
    for (;;)
    {
      return i;
      int j = loadShader(35632, paramString2);
      if (j == 0)
      {
        i = 0;
      }
      else
      {
        int k = GLES20.glCreateProgram();
        checkGlError("glCreateProgram");
        if (k == 0)
        {
          i = 0;
        }
        else
        {
          GLES20.glAttachShader(k, i);
          checkGlError("glAttachShader");
          GLES20.glAttachShader(k, j);
          checkGlError("glAttachShader");
          GLES20.glLinkProgram(k);
          paramString1 = new int[1];
          GLES20.glGetProgramiv(k, 35714, paramString1, 0);
          i = k;
          if (paramString1[0] != 1)
          {
            GLES20.glDeleteProgram(k);
            i = 0;
          }
        }
      }
    }
  }
  
  private int loadShader(int paramInt, String paramString)
  {
    int i = GLES20.glCreateShader(paramInt);
    checkGlError("glCreateShader type=" + paramInt);
    GLES20.glShaderSource(i, paramString);
    GLES20.glCompileShader(i);
    paramString = new int[1];
    GLES20.glGetShaderiv(i, 35713, paramString, 0);
    paramInt = i;
    if (paramString[0] == 0)
    {
      GLES20.glDeleteShader(i);
      paramInt = 0;
    }
    return paramInt;
  }
  
  public void checkGlError(String paramString)
  {
    int i = GLES20.glGetError();
    if (i != 0) {
      throw new RuntimeException(paramString + ": glError " + i);
    }
  }
  
  public void drawFrame(SurfaceTexture paramSurfaceTexture, boolean paramBoolean)
  {
    checkGlError("onDrawFrame start");
    paramSurfaceTexture.getTransformMatrix(this.mSTMatrix);
    if (paramBoolean)
    {
      this.mSTMatrix[5] = (-this.mSTMatrix[5]);
      this.mSTMatrix[13] = (1.0F - this.mSTMatrix[13]);
    }
    GLES20.glUseProgram(this.mProgram);
    checkGlError("glUseProgram");
    GLES20.glActiveTexture(33984);
    GLES20.glBindTexture(36197, this.mTextureID);
    this.mTriangleVertices.position(0);
    GLES20.glVertexAttribPointer(this.maPositionHandle, 3, 5126, false, 20, this.mTriangleVertices);
    checkGlError("glVertexAttribPointer maPosition");
    GLES20.glEnableVertexAttribArray(this.maPositionHandle);
    checkGlError("glEnableVertexAttribArray maPositionHandle");
    this.mTriangleVertices.position(3);
    GLES20.glVertexAttribPointer(this.maTextureHandle, 2, 5126, false, 20, this.mTriangleVertices);
    checkGlError("glVertexAttribPointer maTextureHandle");
    GLES20.glEnableVertexAttribArray(this.maTextureHandle);
    checkGlError("glEnableVertexAttribArray maTextureHandle");
    GLES20.glUniformMatrix4fv(this.muSTMatrixHandle, 1, false, this.mSTMatrix, 0);
    GLES20.glUniformMatrix4fv(this.muMVPMatrixHandle, 1, false, this.mMVPMatrix, 0);
    GLES20.glDrawArrays(5, 0, 4);
    checkGlError("glDrawArrays");
    GLES20.glFinish();
  }
  
  public int getTextureId()
  {
    return this.mTextureID;
  }
  
  public void surfaceCreated()
  {
    this.mProgram = createProgram("uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n  gl_Position = uMVPMatrix * aPosition;\n  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n", "#extension GL_OES_EGL_image_external : require\nprecision highp float;\nvarying vec2 vTextureCoord;\nuniform samplerExternalOES sTexture;\nvoid main() {\n  gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n");
    if (this.mProgram == 0) {
      throw new RuntimeException("failed creating program");
    }
    this.maPositionHandle = GLES20.glGetAttribLocation(this.mProgram, "aPosition");
    checkGlError("glGetAttribLocation aPosition");
    if (this.maPositionHandle == -1) {
      throw new RuntimeException("Could not get attrib location for aPosition");
    }
    this.maTextureHandle = GLES20.glGetAttribLocation(this.mProgram, "aTextureCoord");
    checkGlError("glGetAttribLocation aTextureCoord");
    if (this.maTextureHandle == -1) {
      throw new RuntimeException("Could not get attrib location for aTextureCoord");
    }
    this.muMVPMatrixHandle = GLES20.glGetUniformLocation(this.mProgram, "uMVPMatrix");
    checkGlError("glGetUniformLocation uMVPMatrix");
    if (this.muMVPMatrixHandle == -1) {
      throw new RuntimeException("Could not get attrib location for uMVPMatrix");
    }
    this.muSTMatrixHandle = GLES20.glGetUniformLocation(this.mProgram, "uSTMatrix");
    checkGlError("glGetUniformLocation uSTMatrix");
    if (this.muSTMatrixHandle == -1) {
      throw new RuntimeException("Could not get attrib location for uSTMatrix");
    }
    int[] arrayOfInt = new int[1];
    GLES20.glGenTextures(1, arrayOfInt, 0);
    this.mTextureID = arrayOfInt[0];
    GLES20.glBindTexture(36197, this.mTextureID);
    checkGlError("glBindTexture mTextureID");
    GLES20.glTexParameteri(36197, 10241, 9729);
    GLES20.glTexParameteri(36197, 10240, 9729);
    GLES20.glTexParameteri(36197, 10242, 33071);
    GLES20.glTexParameteri(36197, 10243, 33071);
    checkGlError("glTexParameter");
    Matrix.setIdentityM(this.mMVPMatrix, 0);
    if (this.rotationAngle != 0) {
      Matrix.rotateM(this.mMVPMatrix, 0, this.rotationAngle, 0.0F, 0.0F, 1.0F);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/video/TextureRenderer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */