package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.telegram.messenger.support.widget.RecyclerView.State;
import org.telegram.ui.ActionBar.Theme;

public class GroupCreateDividerItemDecoration
  extends RecyclerView.ItemDecoration
{
  private boolean searching;
  private boolean single;
  
  public void getItemOffsets(Rect paramRect, View paramView, RecyclerView paramRecyclerView, RecyclerView.State paramState)
  {
    super.getItemOffsets(paramRect, paramView, paramRecyclerView, paramState);
    paramRect.top = 1;
  }
  
  public void onDraw(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.State paramState)
  {
    int i = paramRecyclerView.getWidth();
    int j = paramRecyclerView.getChildCount();
    int k;
    int m;
    label25:
    int n;
    float f1;
    label63:
    float f2;
    if (this.single)
    {
      k = 0;
      m = 0;
      if (m >= j - k) {
        return;
      }
      paramState = paramRecyclerView.getChildAt(m);
      paramRecyclerView.getChildAdapterPosition(paramState);
      n = paramState.getBottom();
      if (!LocaleController.isRTL) {
        break label113;
      }
      f1 = 0.0F;
      f2 = n;
      if (!LocaleController.isRTL) {
        break label124;
      }
    }
    label113:
    label124:
    for (int i1 = AndroidUtilities.dp(72.0F);; i1 = 0)
    {
      paramCanvas.drawLine(f1, f2, i - i1, n, Theme.dividerPaint);
      m++;
      break label25;
      k = 1;
      break;
      f1 = AndroidUtilities.dp(72.0F);
      break label63;
    }
  }
  
  public void setSearching(boolean paramBoolean)
  {
    this.searching = paramBoolean;
  }
  
  public void setSingle(boolean paramBoolean)
  {
    this.single = paramBoolean;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/GroupCreateDividerItemDecoration.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */