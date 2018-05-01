package org.telegram.ui.ActionBar;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.animation.DecelerateInterpolator;
import org.telegram.messenger.AndroidUtilities;

public class MenuDrawable
  extends Drawable
{
  private boolean animationInProgress;
  private int currentAnimationTime;
  private float currentRotation;
  private float finalRotation;
  private DecelerateInterpolator interpolator = new DecelerateInterpolator();
  private long lastFrameTime;
  private Paint paint = new Paint(1);
  private boolean reverseAngle = false;
  
  public MenuDrawable()
  {
    this.paint.setColor(-1);
    this.paint.setStrokeWidth(AndroidUtilities.dp(2.0F));
  }
  
  public void draw(Canvas paramCanvas)
  {
    float f1;
    if (this.currentRotation != this.finalRotation)
    {
      if (this.lastFrameTime != 0L)
      {
        long l1 = System.currentTimeMillis();
        long l2 = this.lastFrameTime;
        this.currentAnimationTime = ((int)(this.currentAnimationTime + (l1 - l2)));
        if (this.currentAnimationTime >= 300) {
          this.currentRotation = this.finalRotation;
        }
      }
      else
      {
        this.lastFrameTime = System.currentTimeMillis();
        invalidateSelf();
      }
    }
    else
    {
      paramCanvas.save();
      paramCanvas.translate(getIntrinsicWidth() / 2, getIntrinsicHeight() / 2);
      f1 = this.currentRotation;
      if (!this.reverseAngle) {
        break label365;
      }
    }
    label365:
    for (int i = 65356;; i = 180)
    {
      paramCanvas.rotate(i * f1);
      paramCanvas.drawLine(-AndroidUtilities.dp(9.0F), 0.0F, AndroidUtilities.dp(9.0F) - AndroidUtilities.dp(3.0F) * this.currentRotation, 0.0F, this.paint);
      float f2 = AndroidUtilities.dp(5.0F) * (1.0F - Math.abs(this.currentRotation)) - AndroidUtilities.dp(0.5F) * Math.abs(this.currentRotation);
      f1 = AndroidUtilities.dp(9.0F) - AndroidUtilities.dp(2.5F) * Math.abs(this.currentRotation);
      float f3 = AndroidUtilities.dp(5.0F) + AndroidUtilities.dp(2.0F) * Math.abs(this.currentRotation);
      float f4 = -AndroidUtilities.dp(9.0F) + AndroidUtilities.dp(7.5F) * Math.abs(this.currentRotation);
      paramCanvas.drawLine(f4, -f3, f1, -f2, this.paint);
      paramCanvas.drawLine(f4, f3, f1, f2, this.paint);
      paramCanvas.restore();
      return;
      if (this.currentRotation < this.finalRotation)
      {
        this.currentRotation = (this.interpolator.getInterpolation(this.currentAnimationTime / 300.0F) * this.finalRotation);
        break;
      }
      this.currentRotation = (1.0F - this.interpolator.getInterpolation(this.currentAnimationTime / 300.0F));
      break;
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
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    this.paint.setColorFilter(paramColorFilter);
  }
  
  public void setRotation(float paramFloat, boolean paramBoolean)
  {
    this.lastFrameTime = 0L;
    if (this.currentRotation == 1.0F)
    {
      this.reverseAngle = true;
      this.lastFrameTime = 0L;
      if (!paramBoolean) {
        break label100;
      }
      if (this.currentRotation >= paramFloat) {
        break label83;
      }
      this.currentAnimationTime = ((int)(this.currentRotation * 300.0F));
      label49:
      this.lastFrameTime = System.currentTimeMillis();
    }
    for (this.finalRotation = paramFloat;; this.finalRotation = paramFloat)
    {
      invalidateSelf();
      return;
      if (this.currentRotation != 0.0F) {
        break;
      }
      this.reverseAngle = false;
      break;
      label83:
      this.currentAnimationTime = ((int)((1.0F - this.currentRotation) * 300.0F));
      break label49;
      label100:
      this.currentRotation = paramFloat;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ActionBar/MenuDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */