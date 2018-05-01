package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View.MeasureSpec;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class HashtagSearchCell
  extends TextView
{
  private boolean needDivider;
  
  public HashtagSearchCell(Context paramContext)
  {
    super(paramContext);
    setGravity(16);
    setPadding(AndroidUtilities.dp(16.0F), 0, AndroidUtilities.dp(16.0F), 0);
    setTextSize(1, 17.0F);
    setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    if (this.needDivider) {
      paramCanvas.drawLine(0.0F, getHeight() - 1, getWidth(), getHeight() - 1, Theme.dividerPaint);
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    setMeasuredDimension(View.MeasureSpec.getSize(paramInt1), AndroidUtilities.dp(48.0F) + 1);
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