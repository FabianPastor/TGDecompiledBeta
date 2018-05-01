package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class SeekBarView
  extends FrameLayout
{
  private float bufferedProgress;
  private SeekBarViewDelegate delegate;
  private Paint innerPaint1;
  private Paint outerPaint1;
  private boolean pressed;
  private float progressToSet;
  private boolean reportChanges;
  private int thumbDX;
  private int thumbHeight;
  private int thumbWidth;
  private int thumbX;
  
  public SeekBarView(Context paramContext)
  {
    super(paramContext);
    setWillNotDraw(false);
    this.innerPaint1 = new Paint(1);
    this.innerPaint1.setColor(Theme.getColor("player_progressBackground"));
    this.outerPaint1 = new Paint(1);
    this.outerPaint1.setColor(Theme.getColor("player_progress"));
    this.thumbWidth = AndroidUtilities.dp(24.0F);
    this.thumbHeight = AndroidUtilities.dp(24.0F);
  }
  
  public boolean isDragging()
  {
    return this.pressed;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    int i = (getMeasuredHeight() - this.thumbHeight) / 2;
    paramCanvas.drawRect(this.thumbWidth / 2, getMeasuredHeight() / 2 - AndroidUtilities.dp(1.0F), getMeasuredWidth() - this.thumbWidth / 2, getMeasuredHeight() / 2 + AndroidUtilities.dp(1.0F), this.innerPaint1);
    if (this.bufferedProgress > 0.0F)
    {
      f1 = this.thumbWidth / 2;
      f2 = getMeasuredHeight() / 2 - AndroidUtilities.dp(1.0F);
      f3 = this.thumbWidth / 2;
      paramCanvas.drawRect(f1, f2, this.bufferedProgress * (getMeasuredWidth() - this.thumbWidth) + f3, getMeasuredHeight() / 2 + AndroidUtilities.dp(1.0F), this.innerPaint1);
    }
    paramCanvas.drawRect(this.thumbWidth / 2, getMeasuredHeight() / 2 - AndroidUtilities.dp(1.0F), this.thumbWidth / 2 + this.thumbX, getMeasuredHeight() / 2 + AndroidUtilities.dp(1.0F), this.outerPaint1);
    float f1 = this.thumbX + this.thumbWidth / 2;
    float f2 = this.thumbHeight / 2 + i;
    if (this.pressed) {}
    for (float f3 = 8.0F;; f3 = 6.0F)
    {
      paramCanvas.drawCircle(f1, f2, AndroidUtilities.dp(f3), this.outerPaint1);
      return;
    }
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    return onTouch(paramMotionEvent);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    if ((this.progressToSet >= 0.0F) && (getMeasuredWidth() > 0))
    {
      setProgress(this.progressToSet);
      this.progressToSet = -1.0F;
    }
  }
  
  boolean onTouch(MotionEvent paramMotionEvent)
  {
    boolean bool = true;
    if (paramMotionEvent.getAction() == 0)
    {
      getParent().requestDisallowInterceptTouchEvent(true);
      int i = (getMeasuredHeight() - this.thumbWidth) / 2;
      if ((paramMotionEvent.getY() < 0.0F) || (paramMotionEvent.getY() > getMeasuredHeight())) {
        break label355;
      }
      if ((this.thumbX - i > paramMotionEvent.getX()) || (paramMotionEvent.getX() > this.thumbX + this.thumbWidth + i))
      {
        this.thumbX = ((int)paramMotionEvent.getX() - this.thumbWidth / 2);
        if (this.thumbX < 0) {
          this.thumbX = 0;
        }
      }
      else
      {
        this.thumbDX = ((int)(paramMotionEvent.getX() - this.thumbX));
        this.pressed = true;
        invalidate();
      }
    }
    for (;;)
    {
      return bool;
      if (this.thumbX <= getMeasuredWidth() - this.thumbWidth) {
        break;
      }
      this.thumbX = (getMeasuredWidth() - this.thumbWidth);
      break;
      if ((paramMotionEvent.getAction() == 1) || (paramMotionEvent.getAction() == 3))
      {
        if (this.pressed)
        {
          if (paramMotionEvent.getAction() == 1) {
            this.delegate.onSeekBarDrag(this.thumbX / (getMeasuredWidth() - this.thumbWidth));
          }
          this.pressed = false;
          invalidate();
        }
      }
      else if ((paramMotionEvent.getAction() == 2) && (this.pressed))
      {
        this.thumbX = ((int)(paramMotionEvent.getX() - this.thumbDX));
        if (this.thumbX < 0) {
          this.thumbX = 0;
        }
        for (;;)
        {
          if (this.reportChanges) {
            this.delegate.onSeekBarDrag(this.thumbX / (getMeasuredWidth() - this.thumbWidth));
          }
          invalidate();
          break;
          if (this.thumbX > getMeasuredWidth() - this.thumbWidth) {
            this.thumbX = (getMeasuredWidth() - this.thumbWidth);
          }
        }
      }
      label355:
      bool = false;
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    return onTouch(paramMotionEvent);
  }
  
  public void setBufferedProgress(float paramFloat)
  {
    this.bufferedProgress = paramFloat;
  }
  
  public void setColors(int paramInt1, int paramInt2)
  {
    this.innerPaint1.setColor(paramInt1);
    this.outerPaint1.setColor(paramInt2);
  }
  
  public void setDelegate(SeekBarViewDelegate paramSeekBarViewDelegate)
  {
    this.delegate = paramSeekBarViewDelegate;
  }
  
  public void setInnerColor(int paramInt)
  {
    this.innerPaint1.setColor(paramInt);
  }
  
  public void setOuterColor(int paramInt)
  {
    this.outerPaint1.setColor(paramInt);
  }
  
  public void setProgress(float paramFloat)
  {
    if (getMeasuredWidth() == 0) {
      this.progressToSet = paramFloat;
    }
    int i;
    do
    {
      return;
      this.progressToSet = -1.0F;
      i = (int)Math.ceil((getMeasuredWidth() - this.thumbWidth) * paramFloat);
    } while (this.thumbX == i);
    this.thumbX = i;
    if (this.thumbX < 0) {
      this.thumbX = 0;
    }
    for (;;)
    {
      invalidate();
      break;
      if (this.thumbX > getMeasuredWidth() - this.thumbWidth) {
        this.thumbX = (getMeasuredWidth() - this.thumbWidth);
      }
    }
  }
  
  public void setReportChanges(boolean paramBoolean)
  {
    this.reportChanges = paramBoolean;
  }
  
  public static abstract interface SeekBarViewDelegate
  {
    public abstract void onSeekBarDrag(float paramFloat);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/SeekBarView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */