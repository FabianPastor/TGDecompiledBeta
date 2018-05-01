package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class SendingFileDrawable
  extends StatusDrawable
{
  private boolean isChat = false;
  private long lastUpdateTime = 0L;
  private float progress;
  private boolean started = false;
  
  private void update()
  {
    long l1 = System.currentTimeMillis();
    long l2 = l1 - this.lastUpdateTime;
    this.lastUpdateTime = l1;
    l1 = l2;
    if (l2 > 50L) {
      l1 = 50L;
    }
    for (this.progress += (float)l1 / 500.0F; this.progress > 1.0F; this.progress -= 1.0F) {}
    invalidateSelf();
  }
  
  public void draw(Canvas paramCanvas)
  {
    int i = 0;
    if (i < 3)
    {
      label25:
      float f1;
      label57:
      float f3;
      float f4;
      if (i == 0)
      {
        Theme.chat_statusRecordPaint.setAlpha((int)(this.progress * 255.0F));
        f1 = AndroidUtilities.dp(5.0F) * i + AndroidUtilities.dp(5.0F) * this.progress;
        if (!this.isChat) {
          break label204;
        }
        f2 = 3.0F;
        f3 = AndroidUtilities.dp(f2);
        f4 = AndroidUtilities.dp(4.0F);
        if (!this.isChat) {
          break label211;
        }
        f2 = 7.0F;
        label84:
        paramCanvas.drawLine(f1, f3, f1 + f4, AndroidUtilities.dp(f2), Theme.chat_statusRecordPaint);
        if (!this.isChat) {
          break label218;
        }
        f2 = 11.0F;
        label115:
        f4 = AndroidUtilities.dp(f2);
        f3 = AndroidUtilities.dp(4.0F);
        if (!this.isChat) {
          break label225;
        }
      }
      label204:
      label211:
      label218:
      label225:
      for (float f2 = 7.0F;; f2 = 8.0F)
      {
        paramCanvas.drawLine(f1, f4, f1 + f3, AndroidUtilities.dp(f2), Theme.chat_statusRecordPaint);
        i++;
        break;
        if (i == 2)
        {
          Theme.chat_statusRecordPaint.setAlpha((int)((1.0F - this.progress) * 255.0F));
          break label25;
        }
        Theme.chat_statusRecordPaint.setAlpha(255);
        break label25;
        f2 = 4.0F;
        break label57;
        f2 = 8.0F;
        break label84;
        f2 = 12.0F;
        break label115;
      }
    }
    if (this.started) {
      update();
    }
  }
  
  public int getIntrinsicHeight()
  {
    return AndroidUtilities.dp(14.0F);
  }
  
  public int getIntrinsicWidth()
  {
    return AndroidUtilities.dp(18.0F);
  }
  
  public int getOpacity()
  {
    return 0;
  }
  
  public void setAlpha(int paramInt) {}
  
  public void setColorFilter(ColorFilter paramColorFilter) {}
  
  public void setIsChat(boolean paramBoolean)
  {
    this.isChat = paramBoolean;
  }
  
  public void start()
  {
    this.lastUpdateTime = System.currentTimeMillis();
    this.started = true;
    invalidateSelf();
  }
  
  public void stop()
  {
    this.started = false;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/SendingFileDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */