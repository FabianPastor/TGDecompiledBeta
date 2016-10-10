package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import org.telegram.messenger.AndroidUtilities;

public class SendingFileEx2Drawable
  extends Drawable
{
  private boolean isChat = false;
  private long lastUpdateTime = 0L;
  private Paint paint = new Paint(1);
  private float progress;
  private boolean started = false;
  
  public SendingFileEx2Drawable()
  {
    this.paint.setColor(-2758409);
    this.paint.setStyle(Paint.Style.STROKE);
    this.paint.setStrokeWidth(AndroidUtilities.dp(3.0F));
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
    for (this.progress += (float)l1 / 1000.0F; this.progress > 1.0F; this.progress -= 1.0F) {}
    invalidateSelf();
  }
  
  public void draw(Canvas paramCanvas)
  {
    float f2 = 11.0F;
    label40:
    float f3;
    label59:
    float f4;
    float f5;
    if (this.progress <= 0.5F)
    {
      f1 = AndroidUtilities.dp(1.0F);
      int i = (int)f1;
      if (this.progress < 0.5F) {
        break label131;
      }
      f1 = AndroidUtilities.dp(11.0F);
      int j = (int)f1;
      f3 = i;
      if (!this.isChat) {
        break label148;
      }
      f1 = 11.0F;
      f4 = AndroidUtilities.dp(f1);
      f5 = j;
      if (!this.isChat) {
        break label154;
      }
    }
    label131:
    label148:
    label154:
    for (float f1 = f2;; f1 = 12.0F)
    {
      paramCanvas.drawLine(f3, f4, f5, AndroidUtilities.dp(f1), this.paint);
      if (this.started) {
        update();
      }
      return;
      f1 = AndroidUtilities.dp(11.0F) * (this.progress - 0.5F) * 2.0F;
      break;
      f1 = AndroidUtilities.dp(11.0F) * this.progress * 2.0F;
      break label40;
      f1 = 12.0F;
      break label59;
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/SendingFileEx2Drawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */