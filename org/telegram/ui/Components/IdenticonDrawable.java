package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.TLRPC.EncryptedChat;

public class IdenticonDrawable
  extends Drawable
{
  private int[] colors = { -1, -2758925, -13805707, -13657655 };
  private byte[] data;
  private Paint paint = new Paint();
  
  private int getBits(int paramInt)
  {
    return this.data[(paramInt / 8)] >> paramInt % 8 & 0x3;
  }
  
  public void draw(Canvas paramCanvas)
  {
    if (this.data == null) {}
    for (;;)
    {
      return;
      int k;
      float f1;
      float f2;
      float f3;
      int i;
      int j;
      int m;
      if (this.data.length == 16)
      {
        k = 0;
        f1 = (float)Math.floor(Math.min(getBounds().width(), getBounds().height()) / 8.0F);
        f2 = Math.max(0.0F, (getBounds().width() - 8.0F * f1) / 2.0F);
        f3 = Math.max(0.0F, (getBounds().height() - 8.0F * f1) / 2.0F);
        i = 0;
        while (i < 8)
        {
          j = 0;
          while (j < 8)
          {
            m = getBits(k);
            k += 2;
            m = Math.abs(m);
            this.paint.setColor(this.colors[(m % 4)]);
            paramCanvas.drawRect(f2 + j * f1, i * f1 + f3, j * f1 + f2 + f1, i * f1 + f1 + f3, this.paint);
            j += 1;
          }
          i += 1;
        }
      }
      else
      {
        j = 0;
        f1 = (float)Math.floor(Math.min(getBounds().width(), getBounds().height()) / 12.0F);
        f2 = Math.max(0.0F, (getBounds().width() - 12.0F * f1) / 2.0F);
        f3 = Math.max(0.0F, (getBounds().height() - 12.0F * f1) / 2.0F);
        i = 0;
        while (i < 12)
        {
          k = 0;
          while (k < 12)
          {
            m = Math.abs(getBits(j));
            this.paint.setColor(this.colors[(m % 4)]);
            paramCanvas.drawRect(f2 + k * f1, i * f1 + f3, k * f1 + f2 + f1, i * f1 + f1 + f3, this.paint);
            j += 2;
            k += 1;
          }
          i += 1;
        }
      }
    }
  }
  
  public int getIntrinsicHeight()
  {
    return AndroidUtilities.dp(32.0F);
  }
  
  public int getIntrinsicWidth()
  {
    return AndroidUtilities.dp(32.0F);
  }
  
  public int getOpacity()
  {
    return 0;
  }
  
  public void setAlpha(int paramInt) {}
  
  public void setColorFilter(ColorFilter paramColorFilter) {}
  
  public void setEncryptedChat(TLRPC.EncryptedChat paramEncryptedChat)
  {
    this.data = paramEncryptedChat.key_hash;
    if (this.data == null)
    {
      byte[] arrayOfByte = AndroidUtilities.calcAuthKeyHash(paramEncryptedChat.auth_key);
      this.data = arrayOfByte;
      paramEncryptedChat.key_hash = arrayOfByte;
    }
    invalidateSelf();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/IdenticonDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */