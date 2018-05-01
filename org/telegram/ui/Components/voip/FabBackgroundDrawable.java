package org.telegram.ui.Components.voip;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import org.telegram.messenger.AndroidUtilities;

public class FabBackgroundDrawable
  extends Drawable
{
  private Paint bgPaint = new Paint(1);
  private Bitmap shadowBitmap;
  private Paint shadowPaint = new Paint();
  
  public FabBackgroundDrawable()
  {
    this.shadowPaint.setColor(NUM);
  }
  
  public void draw(Canvas paramCanvas)
  {
    if (this.shadowBitmap == null) {
      onBoundsChange(getBounds());
    }
    int i = Math.min(getBounds().width(), getBounds().height());
    if (this.shadowBitmap != null) {
      paramCanvas.drawBitmap(this.shadowBitmap, getBounds().centerX() - this.shadowBitmap.getWidth() / 2, getBounds().centerY() - this.shadowBitmap.getHeight() / 2, this.shadowPaint);
    }
    paramCanvas.drawCircle(i / 2, i / 2, i / 2 - AndroidUtilities.dp(4.0F), this.bgPaint);
  }
  
  public int getOpacity()
  {
    return 0;
  }
  
  public boolean getPadding(Rect paramRect)
  {
    int i = AndroidUtilities.dp(4.0F);
    paramRect.set(i, i, i, i);
    return true;
  }
  
  protected void onBoundsChange(Rect paramRect)
  {
    int i = Math.min(paramRect.width(), paramRect.height());
    if (i <= 0) {
      this.shadowBitmap = null;
    }
    for (;;)
    {
      return;
      this.shadowBitmap = Bitmap.createBitmap(i, i, Bitmap.Config.ALPHA_8);
      paramRect = new Canvas(this.shadowBitmap);
      Paint localPaint = new Paint(1);
      localPaint.setShadowLayer(AndroidUtilities.dp(3.33333F), 0.0F, AndroidUtilities.dp(0.666F), -1);
      paramRect.drawCircle(i / 2, i / 2, i / 2 - AndroidUtilities.dp(4.0F), localPaint);
    }
  }
  
  public void setAlpha(int paramInt) {}
  
  public void setColor(int paramInt)
  {
    this.bgPaint.setColor(paramInt);
    invalidateSelf();
  }
  
  public void setColorFilter(ColorFilter paramColorFilter) {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/voip/FabBackgroundDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */