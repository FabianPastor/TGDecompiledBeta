package org.telegram.ui.Components.Paint;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import org.telegram.ui.Components.Size;

public class Texture
{
  private Bitmap bitmap;
  private int texture;
  
  public Texture(Bitmap paramBitmap)
  {
    this.bitmap = paramBitmap;
  }
  
  public static int generateTexture(Size paramSize)
  {
    int[] arrayOfInt = new int[1];
    GLES20.glGenTextures(1, arrayOfInt, 0);
    int i = arrayOfInt[0];
    GLES20.glBindTexture(3553, i);
    GLES20.glTexParameteri(3553, 10242, 33071);
    GLES20.glTexParameteri(3553, 10243, 33071);
    GLES20.glTexParameteri(3553, 10240, 9729);
    GLES20.glTexParameteri(3553, 10241, 9729);
    GLES20.glTexImage2D(3553, 0, 6408, (int)paramSize.width, (int)paramSize.height, 0, 6408, 5121, null);
    return i;
  }
  
  private boolean isPOT(int paramInt)
  {
    if ((paramInt - 1 & paramInt) == 0) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public void cleanResources(boolean paramBoolean)
  {
    if (this.texture == 0) {}
    for (;;)
    {
      return;
      GLES20.glDeleteTextures(1, new int[] { this.texture }, 0);
      this.texture = 0;
      if (paramBoolean) {
        this.bitmap.recycle();
      }
    }
  }
  
  public int texture()
  {
    int i = 9729;
    if (this.texture != 0) {
      i = this.texture;
    }
    for (;;)
    {
      return i;
      if (this.bitmap.isRecycled())
      {
        i = 0;
      }
      else
      {
        int[] arrayOfInt = new int[1];
        GLES20.glGenTextures(1, arrayOfInt, 0);
        this.texture = arrayOfInt[0];
        GLES20.glBindTexture(3553, this.texture);
        GLES20.glTexParameteri(3553, 10242, 33071);
        GLES20.glTexParameteri(3553, 10243, 33071);
        GLES20.glTexParameteri(3553, 10240, 9729);
        if (0 != 0) {
          i = 9987;
        }
        GLES20.glTexParameteri(3553, 10241, i);
        GLUtils.texImage2D(3553, 0, this.bitmap, 0);
        if (0 != 0) {
          GLES20.glGenerateMipmap(3553);
        }
        Utils.HasGLError();
        i = this.texture;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/Paint/Texture.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */