package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Adapters.BaseSectionsAdapter;

public class SectionsListView
  extends ListView
  implements AbsListView.OnScrollListener
{
  private int currentStartSection = -1;
  private BaseSectionsAdapter mAdapter;
  private AbsListView.OnScrollListener mOnScrollListener;
  private View pinnedHeader;
  
  public SectionsListView(Context paramContext)
  {
    super(paramContext);
    super.setOnScrollListener(this);
  }
  
  public SectionsListView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    super.setOnScrollListener(this);
  }
  
  public SectionsListView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    super.setOnScrollListener(this);
  }
  
  private void ensurePinnedHeaderLayout(View paramView, boolean paramBoolean)
  {
    int i;
    int j;
    if ((paramView.isLayoutRequested()) || (paramBoolean))
    {
      i = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), NUM);
      j = View.MeasureSpec.makeMeasureSpec(0, 0);
    }
    try
    {
      paramView.measure(i, j);
      paramView.layout(0, 0, paramView.getMeasuredWidth(), paramView.getMeasuredHeight());
      return;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e("tmessages", localException);
      }
    }
  }
  
  private View getSectionHeaderView(int paramInt, View paramView)
  {
    if (paramView == null) {}
    for (int i = 1;; i = 0)
    {
      paramView = this.mAdapter.getSectionHeaderView(paramInt, paramView, this);
      if (i != 0) {
        ensurePinnedHeaderLayout(paramView, false);
      }
      return paramView;
    }
  }
  
  protected void dispatchDraw(Canvas paramCanvas)
  {
    super.dispatchDraw(paramCanvas);
    if ((this.mAdapter == null) || (this.pinnedHeader == null)) {
      return;
    }
    int i = paramCanvas.save();
    int j = ((Integer)this.pinnedHeader.getTag()).intValue();
    if (LocaleController.isRTL) {}
    for (float f = getWidth() - this.pinnedHeader.getWidth();; f = 0.0F)
    {
      paramCanvas.translate(f, j);
      paramCanvas.clipRect(0, 0, getWidth(), this.pinnedHeader.getMeasuredHeight());
      this.pinnedHeader.draw(paramCanvas);
      paramCanvas.restoreToCount(i);
      return;
    }
  }
  
  public void onScroll(AbsListView paramAbsListView, int paramInt1, int paramInt2, int paramInt3)
  {
    if (this.mOnScrollListener != null) {
      this.mOnScrollListener.onScroll(paramAbsListView, paramInt1, paramInt2, paramInt3);
    }
    if (this.mAdapter == null) {}
    while (this.mAdapter.getCount() == 0) {
      return;
    }
    paramInt2 = this.mAdapter.getSectionForPosition(paramInt1);
    if ((this.currentStartSection != paramInt2) || (this.pinnedHeader == null))
    {
      this.pinnedHeader = getSectionHeaderView(paramInt2, this.pinnedHeader);
      this.currentStartSection = paramInt2;
    }
    paramInt2 = this.mAdapter.getCountForSection(paramInt2);
    if (this.mAdapter.getPositionInSectionForPosition(paramInt1) == paramInt2 - 1)
    {
      paramAbsListView = getChildAt(0);
      paramInt2 = this.pinnedHeader.getHeight();
      paramInt1 = 0;
      if (paramAbsListView != null)
      {
        paramInt3 = paramAbsListView.getTop() + paramAbsListView.getHeight();
        if (paramInt3 < paramInt2) {
          paramInt1 = paramInt3 - paramInt2;
        }
        if (paramInt1 >= 0) {
          break label176;
        }
        this.pinnedHeader.setTag(Integer.valueOf(paramInt1));
      }
    }
    for (;;)
    {
      invalidate();
      return;
      paramInt1 = -AndroidUtilities.dp(100.0F);
      break;
      label176:
      this.pinnedHeader.setTag(Integer.valueOf(0));
      continue;
      this.pinnedHeader.setTag(Integer.valueOf(0));
    }
  }
  
  public void onScrollStateChanged(AbsListView paramAbsListView, int paramInt)
  {
    if (this.mOnScrollListener != null) {
      this.mOnScrollListener.onScrollStateChanged(paramAbsListView, paramInt);
    }
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    if ((this.mAdapter == null) || (this.pinnedHeader == null)) {
      return;
    }
    ensurePinnedHeaderLayout(this.pinnedHeader, true);
  }
  
  public void setAdapter(ListAdapter paramListAdapter)
  {
    if (this.mAdapter == paramListAdapter) {
      return;
    }
    this.pinnedHeader = null;
    if ((paramListAdapter instanceof BaseSectionsAdapter)) {}
    for (this.mAdapter = ((BaseSectionsAdapter)paramListAdapter);; this.mAdapter = null)
    {
      super.setAdapter(paramListAdapter);
      return;
    }
  }
  
  public void setOnItemClickListener(AdapterView.OnItemClickListener paramOnItemClickListener)
  {
    super.setOnItemClickListener(paramOnItemClickListener);
  }
  
  public void setOnScrollListener(AbsListView.OnScrollListener paramOnScrollListener)
  {
    this.mOnScrollListener = paramOnScrollListener;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/SectionsListView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */