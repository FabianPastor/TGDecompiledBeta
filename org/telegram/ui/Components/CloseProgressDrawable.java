package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.drawable.Drawable;
import android.view.animation.DecelerateInterpolator;
import org.telegram.messenger.AndroidUtilities;

public class CloseProgressDrawable
  extends Drawable
{
  private int currentAnimationTime;
  private int currentSegment;
  private DecelerateInterpolator interpolator = new DecelerateInterpolator();
  private long lastFrameTime;
  private Paint paint = new Paint(1);
  
  public CloseProgressDrawable()
  {
    this.paint.setColor(-9079435);
    this.paint.setStrokeWidth(AndroidUtilities.dp(2.0F));
    this.paint.setStrokeCap(Paint.Cap.ROUND);
  }
  
  public void draw(Canvas paramCanvas)
  {
    long l1 = System.currentTimeMillis();
    if (this.lastFrameTime != 0L)
    {
      long l2 = this.lastFrameTime;
      this.currentAnimationTime = ((int)(this.currentAnimationTime + (l1 - l2)));
      if (this.currentAnimationTime > 200)
      {
        this.currentAnimationTime = 0;
        this.currentSegment += 1;
        if (this.currentSegment == 4) {
          this.currentSegment -= 4;
        }
      }
    }
    paramCanvas.save();
    paramCanvas.translate(getIntrinsicWidth() / 2, getIntrinsicHeight() / 2);
    paramCanvas.rotate(45.0F);
    this.paint.setAlpha(255 - this.currentSegment % 4 * 40);
    paramCanvas.drawLine(-AndroidUtilities.dp(8.0F), 0.0F, 0.0F, 0.0F, this.paint);
    this.paint.setAlpha(255 - (this.currentSegment + 1) % 4 * 40);
    paramCanvas.drawLine(0.0F, -AndroidUtilities.dp(8.0F), 0.0F, 0.0F, this.paint);
    this.paint.setAlpha(255 - (this.currentSegment + 2) % 4 * 40);
    paramCanvas.drawLine(0.0F, 0.0F, AndroidUtilities.dp(8.0F), 0.0F, this.paint);
    this.paint.setAlpha(255 - (this.currentSegment + 3) % 4 * 40);
    paramCanvas.drawLine(0.0F, 0.0F, 0.0F, AndroidUtilities.dp(8.0F), this.paint);
    paramCanvas.restore();
    this.lastFrameTime = l1;
    invalidateSelf();
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
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/CloseProgressDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */