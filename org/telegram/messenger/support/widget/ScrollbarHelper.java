package org.telegram.messenger.support.widget;

import android.view.View;

class ScrollbarHelper
{
  static int computeScrollExtent(RecyclerView.State paramState, OrientationHelper paramOrientationHelper, View paramView1, View paramView2, RecyclerView.LayoutManager paramLayoutManager, boolean paramBoolean)
  {
    int i;
    if ((paramLayoutManager.getChildCount() == 0) || (paramState.getItemCount() == 0) || (paramView1 == null) || (paramView2 == null)) {
      i = 0;
    }
    for (;;)
    {
      return i;
      if (!paramBoolean)
      {
        i = Math.abs(paramLayoutManager.getPosition(paramView1) - paramLayoutManager.getPosition(paramView2)) + 1;
      }
      else
      {
        i = paramOrientationHelper.getDecoratedEnd(paramView2);
        int j = paramOrientationHelper.getDecoratedStart(paramView1);
        i = Math.min(paramOrientationHelper.getTotalSpace(), i - j);
      }
    }
  }
  
  static int computeScrollOffset(RecyclerView.State paramState, OrientationHelper paramOrientationHelper, View paramView1, View paramView2, RecyclerView.LayoutManager paramLayoutManager, boolean paramBoolean1, boolean paramBoolean2)
  {
    int i = 0;
    int j = i;
    if (paramLayoutManager.getChildCount() != 0)
    {
      j = i;
      if (paramState.getItemCount() != 0)
      {
        j = i;
        if (paramView1 != null)
        {
          if (paramView2 != null) {
            break label45;
          }
          j = i;
        }
      }
    }
    return j;
    label45:
    i = Math.min(paramLayoutManager.getPosition(paramView1), paramLayoutManager.getPosition(paramView2));
    j = Math.max(paramLayoutManager.getPosition(paramView1), paramLayoutManager.getPosition(paramView2));
    if (paramBoolean2) {}
    for (i = Math.max(0, paramState.getItemCount() - j - 1);; i = Math.max(0, i))
    {
      j = i;
      if (!paramBoolean1) {
        break;
      }
      j = Math.abs(paramOrientationHelper.getDecoratedEnd(paramView2) - paramOrientationHelper.getDecoratedStart(paramView1));
      int k = Math.abs(paramLayoutManager.getPosition(paramView1) - paramLayoutManager.getPosition(paramView2));
      float f = j / (k + 1);
      j = Math.round(i * f + (paramOrientationHelper.getStartAfterPadding() - paramOrientationHelper.getDecoratedStart(paramView1)));
      break;
    }
  }
  
  static int computeScrollRange(RecyclerView.State paramState, OrientationHelper paramOrientationHelper, View paramView1, View paramView2, RecyclerView.LayoutManager paramLayoutManager, boolean paramBoolean)
  {
    int i;
    if ((paramLayoutManager.getChildCount() == 0) || (paramState.getItemCount() == 0) || (paramView1 == null) || (paramView2 == null)) {
      i = 0;
    }
    for (;;)
    {
      return i;
      if (!paramBoolean)
      {
        i = paramState.getItemCount();
      }
      else
      {
        int j = paramOrientationHelper.getDecoratedEnd(paramView2);
        int k = paramOrientationHelper.getDecoratedStart(paramView1);
        i = Math.abs(paramLayoutManager.getPosition(paramView1) - paramLayoutManager.getPosition(paramView2));
        i = (int)((j - k) / (i + 1) * paramState.getItemCount());
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/support/widget/ScrollbarHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */