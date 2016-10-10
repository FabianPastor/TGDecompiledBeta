package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;

public class HashtagSearchCell
  extends TextView
{
  private static Paint paint;
  private boolean needDivider;
  
  public HashtagSearchCell(Context paramContext)
  {
    super(paramContext);
    setGravity(16);
    setPadding(AndroidUtilities.dp(16.0F), 0, AndroidUtilities.dp(16.0F), 0);
    setTextSize(1, 17.0F);
    setTextColor(-16777216);
    if (paint == null)
    {
      paint = new Paint();
      paint.setColor(-2302756);
    }
    setBackgroundResource(2130837796);
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    if (this.needDivider) {
      paramCanvas.drawLine(0.0F, getHeight() - 1, getWidth(), getHeight() - 1, paint);
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    setMeasuredDimension(View.MeasureSpec.getSize(paramInt1), AndroidUtilities.dp(48.0F) + 1);
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((Build.VERSION.SDK_INT >= 21) && (getBackground() != null) && ((paramMotionEvent.getAction() == 0) || (paramMotionEvent.getAction() == 2))) {
      getBackground().setHotspot(paramMotionEvent.getX(), paramMotionEvent.getY());
    }
    return super.onTouchEvent(paramMotionEvent);
  }
  
  public void setNeedDivider(boolean paramBoolean)
  {
    this.needDivider = paramBoolean;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/HashtagSearchCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */