package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import org.telegram.messenger.AndroidUtilities;

public class MapPlaceholderDrawable
  extends Drawable
{
  private Paint linePaint;
  private Paint paint = new Paint();
  
  public MapPlaceholderDrawable()
  {
    this.paint.setColor(-2172970);
    this.linePaint = new Paint();
    this.linePaint.setColor(-3752002);
    this.linePaint.setStrokeWidth(AndroidUtilities.dp(1.0F));
  }
  
  public void draw(Canvas paramCanvas)
  {
    paramCanvas.drawRect(getBounds(), this.paint);
    int i = AndroidUtilities.dp(9.0F);
    int j = getBounds().width() / i;
    int k = getBounds().height() / i;
    int m = getBounds().left;
    int n = getBounds().top;
    for (int i1 = 0; i1 < j; i1++) {
      paramCanvas.drawLine((i1 + 1) * i + m, n, (i1 + 1) * i + m, getBounds().height() + n, this.linePaint);
    }
    for (i1 = 0; i1 < k; i1++) {
      paramCanvas.drawLine(m, (i1 + 1) * i + n, getBounds().width() + m, (i1 + 1) * i + n, this.linePaint);
    }
  }
  
  public int getIntrinsicHeight()
  {
    return 0;
  }
  
  public int getIntrinsicWidth()
  {
    return 0;
  }
  
  public int getOpacity()
  {
    return 0;
  }
  
  public void setAlpha(int paramInt) {}
  
  public void setColorFilter(ColorFilter paramColorFilter) {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/MapPlaceholderDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */