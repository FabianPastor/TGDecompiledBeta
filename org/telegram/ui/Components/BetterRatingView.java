package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class BetterRatingView
  extends View
{
  private Bitmap filledStar = BitmapFactory.decodeResource(getResources(), NUM).extractAlpha();
  private Bitmap hollowStar = BitmapFactory.decodeResource(getResources(), NUM).extractAlpha();
  private OnRatingChangeListener listener;
  private int numStars = 5;
  private Paint paint = new Paint();
  private int selectedRating = 0;
  
  public BetterRatingView(Context paramContext)
  {
    super(paramContext);
  }
  
  public int getRating()
  {
    return this.selectedRating;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    int i = 0;
    if (i < this.numStars)
    {
      Paint localPaint = this.paint;
      if (i < this.selectedRating)
      {
        localObject = "calls_ratingStarSelected";
        label27:
        localPaint.setColor(Theme.getColor((String)localObject));
        if (i >= this.selectedRating) {
          break label82;
        }
      }
      label82:
      for (Object localObject = this.filledStar;; localObject = this.hollowStar)
      {
        paramCanvas.drawBitmap((Bitmap)localObject, AndroidUtilities.dp(48.0F) * i, 0.0F, this.paint);
        i++;
        break;
        localObject = "calls_ratingStar";
        break label27;
      }
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    setMeasuredDimension(this.numStars * AndroidUtilities.dp(32.0F) + (this.numStars - 1) * AndroidUtilities.dp(16.0F), AndroidUtilities.dp(32.0F));
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    float f = AndroidUtilities.dp(-8.0F);
    for (int i = 0;; i++)
    {
      if (i < this.numStars)
      {
        if ((paramMotionEvent.getX() > f) && (paramMotionEvent.getX() < AndroidUtilities.dp(48.0F) + f) && (this.selectedRating != i + 1))
        {
          this.selectedRating = (i + 1);
          if (this.listener != null) {
            this.listener.onRatingChanged(this.selectedRating);
          }
          invalidate();
        }
      }
      else {
        return true;
      }
      f += AndroidUtilities.dp(48.0F);
    }
  }
  
  public void setOnRatingChangeListener(OnRatingChangeListener paramOnRatingChangeListener)
  {
    this.listener = paramOnRatingChangeListener;
  }
  
  public static abstract interface OnRatingChangeListener
  {
    public abstract void onRatingChanged(int paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/BetterRatingView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */