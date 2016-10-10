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
    int j = AndroidUtilities.dp(9.0F);
    int i1 = getBounds().width() / j;
    int k = getBounds().height() / j;
    int m = getBounds().left;
    int n = getBounds().top;
    int i = 0;
    while (i < i1)
    {
      paramCanvas.drawLine((i + 1) * j + m, n, (i + 1) * j + m, getBounds().height() + n, this.linePaint);
      i += 1;
    }
    i = 0;
    while (i < k)
    {
      paramCanvas.drawLine(m, (i + 1) * j + n, getBounds().width() + m, (i + 1) * j + n, this.linePaint);
      i += 1;
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