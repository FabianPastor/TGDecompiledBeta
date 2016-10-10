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
    return (paramInt - 1 & paramInt) == 0;
  }
  
  public void cleanResources(boolean paramBoolean)
  {
    if (this.texture == 0) {}
    do
    {
      return;
      GLES20.glDeleteTextures(1, new int[] { this.texture }, 0);
      this.texture = 0;
    } while (!paramBoolean);
    this.bitmap.recycle();
  }
  
  public int texture()
  {
    int j = 9729;
    int k = 1;
    int i = 0;
    if (this.texture != 0) {
      i = this.texture;
    }
    while (this.bitmap.isRecycled()) {
      return i;
    }
    int[] arrayOfInt = new int[1];
    GLES20.glGenTextures(1, arrayOfInt, 0);
    this.texture = arrayOfInt[0];
    GLES20.glBindTexture(3553, this.texture);
    if ((isPOT(this.bitmap.getWidth())) && (isPOT(this.bitmap.getHeight()))) {}
    for (i = k;; i = 0)
    {
      GLES20.glTexParameteri(3553, 10242, 33071);
      GLES20.glTexParameteri(3553, 10243, 33071);
      GLES20.glTexParameteri(3553, 10240, 9729);
      if (i != 0) {
        j = 9987;
      }
      GLES20.glTexParameteri(3553, 10241, j);
      GLUtils.texImage2D(3553, 0, this.bitmap, 0);
      if (i != 0) {
        GLES20.glGenerateMipmap(3553);
      }
      Utils.HasGLError();
      return this.texture;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/Paint/Texture.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */