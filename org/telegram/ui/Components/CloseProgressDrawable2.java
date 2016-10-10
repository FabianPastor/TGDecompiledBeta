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
    this.paint.setColor(-5395027);
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
          break label397;
        }
        this.angle = 0.0F;
        invalidateSelf();
      }
    }
    paramCanvas.save();
    paramCanvas.translate(getIntrinsicWidth() / 2, getIntrinsicHeight() / 2);
    paramCanvas.rotate(-45.0F);
    float f6 = 1.0F;
    float f8 = 1.0F;
    float f7 = 1.0F;
    float f5 = 0.0F;
    float f1;
    float f4;
    float f3;
    label165:
    RectF localRectF;
    if ((this.angle >= 0.0F) && (this.angle < 90.0F))
    {
      f1 = 1.0F - this.angle / 90.0F;
      f4 = f5;
      f3 = f7;
      f2 = f8;
      if (f1 != 0.0F) {
        paramCanvas.drawLine(0.0F, 0.0F, 0.0F, AndroidUtilities.dp(8.0F) * f1, this.paint);
      }
      if (f2 != 0.0F) {
        paramCanvas.drawLine(-AndroidUtilities.dp(8.0F) * f2, 0.0F, 0.0F, 0.0F, this.paint);
      }
      if (f3 != 0.0F) {
        paramCanvas.drawLine(0.0F, -AndroidUtilities.dp(8.0F) * f3, 0.0F, 0.0F, this.paint);
      }
      if (f4 != 1.0F) {
        paramCanvas.drawLine(AndroidUtilities.dp(8.0F) * f4, 0.0F, AndroidUtilities.dp(8.0F), 0.0F, this.paint);
      }
      paramCanvas.restore();
      int i = getBounds().centerX();
      int j = getBounds().centerY();
      this.rect.set(i - AndroidUtilities.dp(8.0F), j - AndroidUtilities.dp(8.0F), AndroidUtilities.dp(8.0F) + i, AndroidUtilities.dp(8.0F) + j);
      localRectF = this.rect;
      if (this.angle >= 360.0F) {
        break label759;
      }
      f1 = 0.0F;
      label359:
      if (this.angle >= 360.0F) {
        break label770;
      }
    }
    label397:
    label759:
    label770:
    for (float f2 = this.angle;; f2 = 720.0F - this.angle)
    {
      paramCanvas.drawArc(localRectF, f1 - 45.0F, f2, false, this.paint);
      this.lastFrameTime = l1;
      return;
      this.angle -= (int)(this.angle / 720.0F) * 720;
      break;
      if ((this.angle >= 90.0F) && (this.angle < 180.0F))
      {
        f1 = 0.0F;
        f2 = 1.0F - (this.angle - 90.0F) / 90.0F;
        f3 = f7;
        f4 = f5;
        break label165;
      }
      if ((this.angle >= 180.0F) && (this.angle < 270.0F))
      {
        f2 = 0.0F;
        f1 = 0.0F;
        f3 = 1.0F - (this.angle - 180.0F) / 90.0F;
        f4 = f5;
        break label165;
      }
      if ((this.angle >= 270.0F) && (this.angle < 360.0F))
      {
        f3 = 0.0F;
        f2 = 0.0F;
        f1 = 0.0F;
        f4 = (this.angle - 270.0F) / 90.0F;
        break label165;
      }
      if ((this.angle >= 360.0F) && (this.angle < 450.0F))
      {
        f3 = 0.0F;
        f2 = 0.0F;
        f1 = 0.0F;
        f4 = 1.0F - (this.angle - 360.0F) / 90.0F;
        break label165;
      }
      if ((this.angle >= 450.0F) && (this.angle < 540.0F))
      {
        f3 = 0.0F;
        f2 = 0.0F;
        f1 = (this.angle - 450.0F) / 90.0F;
        f4 = f5;
        break label165;
      }
      if ((this.angle >= 540.0F) && (this.angle < 630.0F))
      {
        f3 = 0.0F;
        f2 = (this.angle - 540.0F) / 90.0F;
        f1 = f6;
        f4 = f5;
        break label165;
      }
      f1 = f6;
      f2 = f8;
      f3 = f7;
      f4 = f5;
      if (this.angle < 630.0F) {
        break label165;
      }
      f1 = f6;
      f2 = f8;
      f3 = f7;
      f4 = f5;
      if (this.angle >= 720.0F) {
        break label165;
      }
      f3 = (this.angle - 630.0F) / 90.0F;
      f1 = f6;
      f2 = f8;
      f4 = f5;
      break label165;
      f1 = this.angle - 360.0F;
      break label359;
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
  
  public void setColorFilter(ColorFilter paramColorFilter) {}
  
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