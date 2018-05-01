package org.telegram.ui.Cells;

import android.content.Context;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;

public class EmptyCell
  extends FrameLayout
{
  int cellHeight;
  
  public EmptyCell(Context paramContext)
  {
    this(paramContext, 8);
  }
  
  public EmptyCell(Context paramContext, int paramInt)
  {
    super(paramContext);
    this.cellHeight = paramInt;
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), NUM), View.MeasureSpec.makeMeasureSpec(this.cellHeight, NUM));
  }
  
  public void setHeight(int paramInt)
  {
    this.cellHeight = paramInt;
    requestLayout();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/EmptyCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */