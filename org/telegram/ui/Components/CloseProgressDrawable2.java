package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.animation.DecelerateInterpolator;
import org.telegram.messenger.AndroidUtilities;

public class CloseProgressDrawable2
  extends Drawable
{
  private float angle;
  private boolean animating;
  private DecelerateInterpolator interpolator = new DecelerateInterpolator();
  private long lastFrameTime;
  private Paint paint = new Paint(1);
  private RectF rect = new RectF();
  
  public CloseProgressDrawable2()
  {
    this.paint.setColor(-1);
    this.paint.setStrokeWidth(AndroidUtilities.dp(2.0F));
    this.paint.setStrokeCap(Paint.Cap.ROUND);
    this.paint.setStyle(Paint.Style.STROKE);
  }
  
  public void draw(Canvas paramCanvas)
  {
    long l1 = System.currentTimeMillis();
    if (this.lastFrameTime != 0L)
    {
      long l2 = this.lastFrameTime;
      if ((this.animating) || (this.angle != 0.0F))
      {
        this.angle += (float)(360L * (l1 - l2)) / 500.0F;
        if ((this.animating) || (this.angle < 720.0F)) {
          break label404;
        }
        this.angle = 0.0F;
        invalidateSelf();
      }
    }
    paramCanvas.save();
    paramCanvas.translate(getIntrinsicWidth() / 2, getIntrinsicHeight() / 2);
    paramCanvas.rotate(-45.0F);
    float f1 = 1.0F;
    float f2 = 1.0F;
    float f3 = 1.0F;
    float f4 = 0.0F;
    float f5;
    float f6;
    float f7;
    label165:
    RectF localRectF;
    if ((this.angle >= 0.0F) && (this.angle < 90.0F))
    {
      f5 = 1.0F - this.angle / 90.0F;
      f6 = f4;
      f7 = f3;
      f8 = f2;
      if (f5 != 0.0F) {
        paramCanvas.drawLine(0.0F, 0.0F, 0.0F, AndroidUtilities.dp(8.0F) * f5, this.paint);
      }
      if (f8 != 0.0F) {
        paramCanvas.drawLine(-AndroidUtilities.dp(8.0F) * f8, 0.0F, 0.0F, 0.0F, this.paint);
      }
      if (f7 != 0.0F) {
        paramCanvas.drawLine(0.0F, -AndroidUtilities.dp(8.0F) * f7, 0.0F, 0.0F, this.paint);
      }
      if (f6 != 1.0F) {
        paramCanvas.drawLine(AndroidUtilities.dp(8.0F) * f6, 0.0F, AndroidUtilities.dp(8.0F), 0.0F, this.paint);
      }
      paramCanvas.restore();
      int i = getBounds().centerX();
      int j = getBounds().centerY();
      this.rect.set(i - AndroidUtilities.dp(8.0F), j - AndroidUtilities.dp(8.0F), AndroidUtilities.dp(8.0F) + i, AndroidUtilities.dp(8.0F) + j);
      localRectF = this.rect;
      if (this.angle >= 360.0F) {
        break label784;
      }
      f5 = 0.0F;
      label364:
      if (this.angle >= 360.0F) {
        break label796;
      }
    }
    label404:
    label784:
    label796:
    for (float f8 = this.angle;; f8 = 720.0F - this.angle)
    {
      paramCanvas.drawArc(localRectF, f5 - 45.0F, f8, false, this.paint);
      this.lastFrameTime = l1;
      return;
      this.angle -= (int)(this.angle / 720.0F) * 720;
      break;
      if ((this.angle >= 90.0F) && (this.angle < 180.0F))
      {
        f5 = 0.0F;
        f8 = 1.0F - (this.angle - 90.0F) / 90.0F;
        f7 = f3;
        f6 = f4;
        break label165;
      }
      if ((this.angle >= 180.0F) && (this.angle < 270.0F))
      {
        f8 = 0.0F;
        f5 = 0.0F;
        f7 = 1.0F - (this.angle - 180.0F) / 90.0F;
        f6 = f4;
        break label165;
      }
      if ((this.angle >= 270.0F) && (this.angle < 360.0F))
      {
        f7 = 0.0F;
        f8 = 0.0F;
        f5 = 0.0F;
        f6 = (this.angle - 270.0F) / 90.0F;
        break label165;
      }
      if ((this.angle >= 360.0F) && (this.angle < 450.0F))
      {
        f7 = 0.0F;
        f8 = 0.0F;
        f5 = 0.0F;
        f6 = 1.0F - (this.angle - 360.0F) / 90.0F;
        break label165;
      }
      if ((this.angle >= 450.0F) && (this.angle < 540.0F))
      {
        f7 = 0.0F;
        f8 = 0.0F;
        f5 = (this.angle - 450.0F) / 90.0F;
        f6 = f4;
        break label165;
      }
      if ((this.angle >= 540.0F) && (this.angle < 630.0F))
      {
        f7 = 0.0F;
        f8 = (this.angle - 540.0F) / 90.0F;
        f5 = f1;
        f6 = f4;
        break label165;
      }
      f5 = f1;
      f8 = f2;
      f7 = f3;
      f6 = f4;
      if (this.angle < 630.0F) {
        break label165;
      }
      f5 = f1;
      f8 = f2;
      f7 = f3;
      f6 = f4;
      if (this.angle >= 720.0F) {
        break label165;
      }
      f7 = (this.angle - 630.0F) / 90.0F;
      f5 = f1;
      f8 = f2;
      f6 = f4;
      break label165;
      f5 = this.angle - 360.0F;
      break label364;
    }
  }
  
  public int getIntrinsicHeight()
  {
    return AndroidUtilities.dp(24.0F);
  }
  
  public int getIntrinsicWidth()
  {
    return AndroidUtilities.dp(24.0F);
  }
  
  public int getOpacity()
  {
    return -2;
  }
  
  public void setAlpha(int paramInt) {}
  
  public void setColor(int paramInt)
  {
    this.paint.setColor(paramInt);
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    this.paint.setColorFilter(paramColorFilter);
  }
  
  public void startAnimation()
  {
    this.animating = true;
    this.lastFrameTime = System.currentTimeMillis();
    invalidateSelf();
  }
  
  public void stopAnimation()
  {
    this.animating = false;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/CloseProgressDrawable2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */