package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import org.telegram.messenger.AndroidUtilities;

public class VideoSeekBarView
  extends View
{
  private SeekBarDelegate delegate;
  private Paint paint = new Paint();
  private Paint paint2 = new Paint(1);
  private boolean pressed = false;
  private float progress = 0.0F;
  private int thumbDX = 0;
  private int thumbHeight = AndroidUtilities.dp(12.0F);
  private int thumbWidth = AndroidUtilities.dp(12.0F);
  
  public VideoSeekBarView(Context paramContext)
  {
    super(paramContext);
    this.paint.setColor(-10724260);
    this.paint2.setColor(-1);
  }
  
  public float getProgress()
  {
    return this.progress;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    int i = (getMeasuredHeight() - this.thumbHeight) / 2;
    int j = (int)((getMeasuredWidth() - this.thumbWidth) * this.progress);
    paramCanvas.drawRect(this.thumbWidth / 2, getMeasuredHeight() / 2 - AndroidUtilities.dp(1.0F), getMeasuredWidth() - this.thumbWidth / 2, getMeasuredHeight() / 2 + AndroidUtilities.dp(1.0F), this.paint);
    paramCanvas.drawCircle(this.thumbWidth / 2 + j, this.thumbHeight / 2 + i, this.thumbWidth / 2, this.paint2);
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
          float f2 = paramMotionEvent.getY();
          f3 = (int)((getMeasuredWidth() - this.thumbWidth) * this.progress);
          if (paramMotionEvent.getAction() == 0)
          {
            int i = (getMeasuredHeight() - this.thumbWidth) / 2;
            bool2 = bool1;
            if (f3 - i <= f1)
            {
              bool2 = bool1;
              if (f1 <= this.thumbWidth + f3 + i)
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
              if ((paramMotionEvent.getAction() == 1) && (this.delegate != null)) {
                this.delegate.onSeekBarDrag(f3 / (getMeasuredWidth() - this.thumbWidth));
              }
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
    float f3 = (int)(f1 - this.thumbDX);
    if (f3 < 0.0F) {
      f1 = 0.0F;
    }
    for (;;)
    {
      this.progress = (f1 / (getMeasuredWidth() - this.thumbWidth));
      invalidate();
      bool2 = true;
      break;
      f1 = f3;
      if (f3 > getMeasuredWidth() - this.thumbWidth) {
        f1 = getMeasuredWidth() - this.thumbWidth;
      }
    }
  }
  
  public void setDelegate(SeekBarDelegate paramSeekBarDelegate)
  {
    this.delegate = paramSeekBarDelegate;
  }
  
  public void setProgress(float paramFloat)
  {
    float f;
    if (paramFloat < 0.0F) {
      f = 0.0F;
    }
    for (;;)
    {
      this.progress = f;
      invalidate();
      return;
      f = paramFloat;
      if (paramFloat > 1.0F) {
        f = 1.0F;
      }
    }
  }
  
  public static abstract interface SeekBarDelegate
  {
    public abstract void onSeekBarDrag(float paramFloat);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/VideoSeekBarView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */