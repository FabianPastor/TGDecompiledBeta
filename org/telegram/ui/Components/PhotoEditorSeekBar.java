package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import org.telegram.messenger.AndroidUtilities;

public class PhotoEditorSeekBar
  extends View
{
  private PhotoEditorSeekBarDelegate delegate;
  private Paint innerPaint = new Paint();
  private int maxValue;
  private int minValue;
  private Paint outerPaint = new Paint(1);
  private boolean pressed = false;
  private float progress = 0.0F;
  private int thumbDX = 0;
  private int thumbSize = AndroidUtilities.dp(16.0F);
  
  public PhotoEditorSeekBar(Context paramContext)
  {
    super(paramContext);
    this.innerPaint.setColor(-11711155);
    this.outerPaint.setColor(-1);
  }
  
  public int getProgress()
  {
    return (int)(this.minValue + this.progress * (this.maxValue - this.minValue));
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    int i = (getMeasuredHeight() - this.thumbSize) / 2;
    int j = (int)((getMeasuredWidth() - this.thumbSize) * this.progress);
    paramCanvas.drawRect(this.thumbSize / 2, getMeasuredHeight() / 2 - AndroidUtilities.dp(1.0F), getMeasuredWidth() - this.thumbSize / 2, getMeasuredHeight() / 2 + AndroidUtilities.dp(1.0F), this.innerPaint);
    if (this.minValue == 0) {
      paramCanvas.drawRect(this.thumbSize / 2, getMeasuredHeight() / 2 - AndroidUtilities.dp(1.0F), j, getMeasuredHeight() / 2 + AndroidUtilities.dp(1.0F), this.outerPaint);
    }
    for (;;)
    {
      paramCanvas.drawCircle(this.thumbSize / 2 + j, this.thumbSize / 2 + i, this.thumbSize / 2, this.outerPaint);
      return;
      if (this.progress > 0.5F)
      {
        paramCanvas.drawRect(getMeasuredWidth() / 2 - AndroidUtilities.dp(1.0F), (getMeasuredHeight() - this.thumbSize) / 2, getMeasuredWidth() / 2, (getMeasuredHeight() + this.thumbSize) / 2, this.outerPaint);
        paramCanvas.drawRect(getMeasuredWidth() / 2, getMeasuredHeight() / 2 - AndroidUtilities.dp(1.0F), j, getMeasuredHeight() / 2 + AndroidUtilities.dp(1.0F), this.outerPaint);
      }
      else
      {
        paramCanvas.drawRect(getMeasuredWidth() / 2, (getMeasuredHeight() - this.thumbSize) / 2, getMeasuredWidth() / 2 + AndroidUtilities.dp(1.0F), (getMeasuredHeight() + this.thumbSize) / 2, this.outerPaint);
        paramCanvas.drawRect(j, getMeasuredHeight() / 2 - AndroidUtilities.dp(1.0F), getMeasuredWidth() / 2, getMeasuredHeight() / 2 + AndroidUtilities.dp(1.0F), this.outerPaint);
      }
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool1 = false;
    boolean bool2;
    if (paramMotionEvent == null) {
      bool2 = bool1;
    }
    float f1;
    do
    {
      do
      {
        for (;;)
        {
          return bool2;
          f1 = paramMotionEvent.getX();
          f2 = paramMotionEvent.getY();
          float f3 = (int)((getMeasuredWidth() - this.thumbSize) * this.progress);
          if (paramMotionEvent.getAction() == 0)
          {
            int i = (getMeasuredHeight() - this.thumbSize) / 2;
            bool2 = bool1;
            if (f3 - i <= f1)
            {
              bool2 = bool1;
              if (f1 <= this.thumbSize + f3 + i)
              {
                bool2 = bool1;
                if (f2 >= 0.0F)
                {
                  bool2 = bool1;
                  if (f2 <= getMeasuredHeight())
                  {
                    this.pressed = true;
                    this.thumbDX = ((int)(f1 - f3));
                    getParent().requestDisallowInterceptTouchEvent(true);
                    invalidate();
                    bool2 = true;
                  }
                }
              }
            }
          }
          else
          {
            if ((paramMotionEvent.getAction() != 1) && (paramMotionEvent.getAction() != 3)) {
              break;
            }
            bool2 = bool1;
            if (this.pressed)
            {
              this.pressed = false;
              invalidate();
              bool2 = true;
            }
          }
        }
        bool2 = bool1;
      } while (paramMotionEvent.getAction() != 2);
      bool2 = bool1;
    } while (!this.pressed);
    float f2 = (int)(f1 - this.thumbDX);
    if (f2 < 0.0F) {
      f1 = 0.0F;
    }
    for (;;)
    {
      this.progress = (f1 / (getMeasuredWidth() - this.thumbSize));
      if (this.delegate != null) {
        this.delegate.onProgressChanged(((Integer)getTag()).intValue(), getProgress());
      }
      invalidate();
      bool2 = true;
      break;
      f1 = f2;
      if (f2 > getMeasuredWidth() - this.thumbSize) {
        f1 = getMeasuredWidth() - this.thumbSize;
      }
    }
  }
  
  public void setDelegate(PhotoEditorSeekBarDelegate paramPhotoEditorSeekBarDelegate)
  {
    this.delegate = paramPhotoEditorSeekBarDelegate;
  }
  
  public void setMinMax(int paramInt1, int paramInt2)
  {
    this.minValue = paramInt1;
    this.maxValue = paramInt2;
  }
  
  public void setProgress(int paramInt)
  {
    setProgress(paramInt, true);
  }
  
  public void setProgress(int paramInt, boolean paramBoolean)
  {
    int i;
    if (paramInt < this.minValue) {
      i = this.minValue;
    }
    for (;;)
    {
      this.progress = ((i - this.minValue) / (this.maxValue - this.minValue));
      invalidate();
      if ((paramBoolean) && (this.delegate != null)) {
        this.delegate.onProgressChanged(((Integer)getTag()).intValue(), getProgress());
      }
      return;
      i = paramInt;
      if (paramInt > this.maxValue) {
        i = this.maxValue;
      }
    }
  }
  
  public static abstract interface PhotoEditorSeekBarDelegate
  {
    public abstract void onProgressChanged(int paramInt1, int paramInt2);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/PhotoEditorSeekBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */