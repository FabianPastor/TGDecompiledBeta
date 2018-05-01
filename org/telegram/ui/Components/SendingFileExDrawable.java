package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import org.telegram.messenger.AndroidUtilities;

public class SendingFileExDrawable
  extends Drawable
{
  private boolean isChat = false;
  private long lastUpdateTime = 0L;
  private Paint paint = new Paint(1);
  private float progress;
  private boolean started = false;
  
  public SendingFileExDrawable()
  {
    this.paint.setColor(-2758409);
    this.paint.setStyle(Paint.Style.STROKE);
    this.paint.setStrokeWidth(AndroidUtilities.dp(2.0F));
    this.paint.setStrokeCap(Paint.Cap.ROUND);
  }
  
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
      label29:
      float f2;
      label61:
      float f3;
      float f4;
      if (i == 0)
      {
        this.paint.setAlpha((int)(this.progress * 255.0F));
        f2 = AndroidUtilities.dp(5.0F) * i + AndroidUtilities.dp(5.0F) * this.progress;
        if (!this.isChat) {
          break label209;
        }
        f1 = 3.0F;
        f3 = AndroidUtilities.dp(f1);
        f4 = AndroidUtilities.dp(4.0F);
        if (!this.isChat) {
          break label215;
        }
        f1 = 7.0F;
        label86:
        paramCanvas.drawLine(f2, f3, f2 + f4, AndroidUtilities.dp(f1), this.paint);
        if (!this.isChat) {
          break label221;
        }
        f1 = 11.0F;
        label116:
        f3 = AndroidUtilities.dp(f1);
        f4 = AndroidUtilities.dp(4.0F);
        if (!this.isChat) {
          break label227;
        }
      }
      label209:
      label215:
      label221:
      label227:
      for (float f1 = 7.0F;; f1 = 8.0F)
      {
        paramCanvas.drawLine(f2, f3, f2 + f4, AndroidUtilities.dp(f1), this.paint);
        i += 1;
        break;
        if (i == 2)
        {
          this.paint.setAlpha((int)((1.0F - this.progress) * 255.0F));
          break label29;
        }
        this.paint.setAlpha(255);
        break label29;
        f1 = 4.0F;
        break label61;
        f1 = 8.0F;
        break label86;
        f1 = 12.0F;
        break label116;
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/SendingFileExDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */