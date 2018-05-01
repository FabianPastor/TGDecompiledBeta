package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import org.telegram.messenger.AndroidUtilities;

public class SeekBar
{
  private static Paint paint;
  private static int thumbWidth;
  private int backgroundColor;
  private int backgroundSelectedColor;
  private float bufferedProgress;
  private int cacheColor;
  private int circleColor;
  private SeekBarDelegate delegate;
  private int height;
  private int lineHeight = AndroidUtilities.dp(2.0F);
  private boolean pressed = false;
  private int progressColor;
  private RectF rect = new RectF();
  private boolean selected;
  private int thumbDX = 0;
  private int thumbX = 0;
  private int width;
  
  public SeekBar(Context paramContext)
  {
    if (paint == null)
    {
      paint = new Paint(1);
      thumbWidth = AndroidUtilities.dp(24.0F);
    }
  }
  
  public void draw(Canvas paramCanvas)
  {
    this.rect.set(thumbWidth / 2, this.height / 2 - this.lineHeight / 2, this.width - thumbWidth / 2, this.height / 2 + this.lineHeight / 2);
    Paint localPaint = paint;
    int i;
    label121:
    float f1;
    float f2;
    if (this.selected)
    {
      i = this.backgroundSelectedColor;
      localPaint.setColor(i);
      paramCanvas.drawRoundRect(this.rect, thumbWidth / 2, thumbWidth / 2, paint);
      if (this.bufferedProgress > 0.0F)
      {
        localPaint = paint;
        if (!this.selected) {
          break label365;
        }
        i = this.backgroundSelectedColor;
        localPaint.setColor(i);
        this.rect.set(thumbWidth / 2, this.height / 2 - this.lineHeight / 2, thumbWidth / 2 + this.bufferedProgress * (this.width - thumbWidth), this.height / 2 + this.lineHeight / 2);
        paramCanvas.drawRoundRect(this.rect, thumbWidth / 2, thumbWidth / 2, paint);
      }
      this.rect.set(thumbWidth / 2, this.height / 2 - this.lineHeight / 2, thumbWidth / 2 + this.thumbX, this.height / 2 + this.lineHeight / 2);
      paint.setColor(this.progressColor);
      paramCanvas.drawRoundRect(this.rect, thumbWidth / 2, thumbWidth / 2, paint);
      paint.setColor(this.circleColor);
      f1 = this.thumbX + thumbWidth / 2;
      f2 = this.height / 2;
      if (!this.pressed) {
        break label373;
      }
    }
    label365:
    label373:
    for (float f3 = 8.0F;; f3 = 6.0F)
    {
      paramCanvas.drawCircle(f1, f2, AndroidUtilities.dp(f3), paint);
      return;
      i = this.backgroundColor;
      break;
      i = this.cacheColor;
      break label121;
    }
  }
  
  public float getProgress()
  {
    return this.thumbX / (this.width - thumbWidth);
  }
  
  public boolean isDragging()
  {
    return this.pressed;
  }
  
  public boolean onTouch(int paramInt, float paramFloat1, float paramFloat2)
  {
    boolean bool1 = true;
    boolean bool2;
    if (paramInt == 0)
    {
      paramInt = (this.height - thumbWidth) / 2;
      if ((this.thumbX - paramInt > paramFloat1) || (paramFloat1 > this.thumbX + thumbWidth + paramInt) || (paramFloat2 < 0.0F) || (paramFloat2 > this.height)) {
        break label232;
      }
      this.pressed = true;
      this.thumbDX = ((int)(paramFloat1 - this.thumbX));
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      if ((paramInt == 1) || (paramInt == 3))
      {
        if (this.pressed)
        {
          if ((paramInt == 1) && (this.delegate != null)) {
            this.delegate.onSeekBarDrag(this.thumbX / (this.width - thumbWidth));
          }
          this.pressed = false;
          bool2 = bool1;
        }
      }
      else if ((paramInt == 2) && (this.pressed))
      {
        this.thumbX = ((int)(paramFloat1 - this.thumbDX));
        if (this.thumbX < 0)
        {
          this.thumbX = 0;
          bool2 = bool1;
          continue;
        }
        bool2 = bool1;
        if (this.thumbX <= this.width - thumbWidth) {
          continue;
        }
        this.thumbX = (this.width - thumbWidth);
        bool2 = bool1;
        continue;
      }
      label232:
      bool2 = false;
    }
  }
  
  public void setBufferedProgress(float paramFloat)
  {
    this.bufferedProgress = paramFloat;
  }
  
  public void setColors(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    this.backgroundColor = paramInt1;
    this.cacheColor = paramInt2;
    this.circleColor = paramInt4;
    this.progressColor = paramInt3;
    this.backgroundSelectedColor = paramInt5;
  }
  
  public void setDelegate(SeekBarDelegate paramSeekBarDelegate)
  {
    this.delegate = paramSeekBarDelegate;
  }
  
  public void setLineHeight(int paramInt)
  {
    this.lineHeight = paramInt;
  }
  
  public void setProgress(float paramFloat)
  {
    this.thumbX = ((int)Math.ceil((this.width - thumbWidth) * paramFloat));
    if (this.thumbX < 0) {
      this.thumbX = 0;
    }
    for (;;)
    {
      return;
      if (this.thumbX > this.width - thumbWidth) {
        this.thumbX = (this.width - thumbWidth);
      }
    }
  }
  
  public void setSelected(boolean paramBoolean)
  {
    this.selected = paramBoolean;
  }
  
  public void setSize(int paramInt1, int paramInt2)
  {
    this.width = paramInt1;
    this.height = paramInt2;
  }
  
  public static abstract interface SeekBarDelegate
  {
    public abstract void onSeekBarDrag(float paramFloat);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/SeekBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */