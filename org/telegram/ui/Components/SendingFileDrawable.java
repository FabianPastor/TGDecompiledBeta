package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.animation.DecelerateInterpolator;
import org.telegram.messenger.AndroidUtilities;

public class SendingFileDrawable
  extends Drawable
{
  private static DecelerateInterpolator decelerateInterpolator = null;
  private float animatedProgressValue = 0.0F;
  private float animationProgressStart = 0.0F;
  private RectF cicleRect = new RectF();
  private float currentProgress = 0.0F;
  private long currentProgressTime = 0L;
  private boolean isChat = false;
  private long lastUpdateTime = 0L;
  private Paint paint = new Paint(1);
  private float radOffset = 0.0F;
  private boolean started = false;
  
  public SendingFileDrawable()
  {
    this.paint.setColor(-2758409);
    this.paint.setStyle(Paint.Style.STROKE);
    this.paint.setStrokeWidth(AndroidUtilities.dp(2.0F));
    this.paint.setStrokeCap(Paint.Cap.ROUND);
    decelerateInterpolator = new DecelerateInterpolator();
  }
  
  private void update()
  {
    long l1 = System.currentTimeMillis();
    long l2 = l1 - this.lastUpdateTime;
    this.lastUpdateTime = l1;
    float f;
    if (this.animatedProgressValue != 1.0F)
    {
      this.radOffset += (float)(360L * l2) / 1000.0F;
      f = this.currentProgress - this.animationProgressStart;
      if (f > 0.0F)
      {
        this.currentProgressTime += l2;
        if (this.currentProgressTime < 300L) {
          break label109;
        }
        this.animatedProgressValue = this.currentProgress;
        this.animationProgressStart = this.currentProgress;
        this.currentProgressTime = 0L;
      }
    }
    for (;;)
    {
      invalidateSelf();
      return;
      label109:
      this.animatedProgressValue = (this.animationProgressStart + decelerateInterpolator.getInterpolation((float)this.currentProgressTime / 300.0F) * f);
    }
  }
  
  public void draw(Canvas paramCanvas)
  {
    RectF localRectF = this.cicleRect;
    float f2 = AndroidUtilities.dp(1.0F);
    float f3;
    float f4;
    if (this.isChat)
    {
      f1 = 3.0F;
      f3 = AndroidUtilities.dp(f1);
      f4 = AndroidUtilities.dp(10.0F);
      if (!this.isChat) {
        break label112;
      }
    }
    label112:
    for (float f1 = 11.0F;; f1 = 12.0F)
    {
      localRectF.set(f2, f3, f4, AndroidUtilities.dp(f1));
      paramCanvas.drawArc(this.cicleRect, this.radOffset - 90.0F, Math.max(60.0F, 360.0F * this.animatedProgressValue), false, this.paint);
      if (this.started) {
        update();
      }
      return;
      f1 = 4.0F;
      break;
    }
  }
  
  public int getIntrinsicHeight()
  {
    return AndroidUtilities.dp(14.0F);
  }
  
  public int getIntrinsicWidth()
  {
    return AndroidUtilities.dp(14.0F);
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
  
  public void setProgress(float paramFloat, boolean paramBoolean)
  {
    if (!paramBoolean) {
      this.animatedProgressValue = paramFloat;
    }
    for (this.animationProgressStart = paramFloat;; this.animationProgressStart = this.animatedProgressValue)
    {
      this.currentProgress = paramFloat;
      this.currentProgressTime = 0L;
      invalidateSelf();
      return;
    }
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