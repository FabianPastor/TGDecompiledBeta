package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Adapters.BaseSectionsAdapter;

public class LetterSectionsListView
  extends ListView
  implements AbsListView.OnScrollListener
{
  private int currentFirst = -1;
  private int currentVisible = -1;
  private ArrayList<View> headers = new ArrayList();
  private ArrayList<View> headersCache = new ArrayList();
  private BaseSectionsAdapter mAdapter;
  private AbsListView.OnScrollListener mOnScrollListener;
  private int sectionsCount;
  private int startSection;
  
  public LetterSectionsListView(Context paramContext)
  {
    super(paramContext);
    super.setOnScrollListener(this);
  }
  
  public LetterSectionsListView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    super.setOnScrollListener(this);
  }
  
  public LetterSectionsListView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
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
      ViewGroup.LayoutParams localLayoutParams = paramView.getLayoutParams();
      i = View.MeasureSpec.makeMeasureSpec(localLayoutParams.height, NUM);
      j = View.MeasureSpec.makeMeasureSpec(localLayoutParams.width, NUM);
    }
    try
    {
      paramView.measure(j, i);
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
    if ((this.mAdapter == null) || (this.headers.isEmpty())) {
      return;
    }
    Iterator localIterator = this.headers.iterator();
    label32:
    View localView;
    int i;
    int j;
    if (localIterator.hasNext())
    {
      localView = (View)localIterator.next();
      i = paramCanvas.save();
      j = ((Integer)localView.getTag()).intValue();
      if (!LocaleController.isRTL) {
        break label173;
      }
    }
    label173:
    for (float f = getWidth() - localView.getWidth();; f = 0.0F)
    {
      paramCanvas.translate(f, j);
      paramCanvas.clipRect(0, 0, getWidth(), localView.getMeasuredHeight());
      if (j < 0) {
        paramCanvas.saveLayerAlpha(0.0F, j, localView.getWidth(), paramCanvas.getHeight() + j, (int)(255.0F * (1.0F + j / localView.getMeasuredHeight())), 4);
      }
      localView.draw(paramCanvas);
      paramCanvas.restoreToCount(i);
      break label32;
      break;
    }
  }
  
  public void onScroll(AbsListView paramAbsListView, int paramInt1, int paramInt2, int paramInt3)
  {
    if (this.mOnScrollListener != null) {
      this.mOnScrollListener.onScroll(paramAbsListView, paramInt1, paramInt2, paramInt3);
    }
    if (this.mAdapter == null) {}
    do
    {
      return;
      this.headersCache.addAll(this.headers);
      this.headers.clear();
    } while (this.mAdapter.getCount() == 0);
    label125:
    label141:
    int j;
    int i;
    if ((this.currentFirst != paramInt1) || (this.currentVisible != paramInt2))
    {
      this.currentFirst = paramInt1;
      this.currentVisible = paramInt2;
      this.sectionsCount = 1;
      this.startSection = this.mAdapter.getSectionForPosition(paramInt1);
      paramInt3 = this.mAdapter.getCountForSection(this.startSection) + paramInt1 - this.mAdapter.getPositionInSectionForPosition(paramInt1);
      if (paramInt3 < paramInt1 + paramInt2) {}
    }
    else
    {
      paramInt2 = paramInt1;
      paramInt3 = this.startSection;
      if (paramInt3 < this.startSection + this.sectionsCount)
      {
        paramAbsListView = null;
        if (!this.headersCache.isEmpty())
        {
          paramAbsListView = (View)this.headersCache.get(0);
          this.headersCache.remove(0);
        }
        paramAbsListView = getSectionHeaderView(paramInt3, paramAbsListView);
        this.headers.add(paramAbsListView);
        j = this.mAdapter.getCountForSection(paramInt3);
        if (paramInt3 != this.startSection) {
          break label393;
        }
        i = this.mAdapter.getPositionInSectionForPosition(paramInt2);
        if (i != j - 1) {
          break label313;
        }
        paramAbsListView.setTag(Integer.valueOf(-paramAbsListView.getHeight()));
      }
    }
    for (;;)
    {
      paramInt2 += j - this.mAdapter.getPositionInSectionForPosition(paramInt1);
      paramInt3 += 1;
      break label141;
      break;
      paramInt3 += this.mAdapter.getCountForSection(this.startSection + this.sectionsCount);
      this.sectionsCount += 1;
      break label125;
      label313:
      if (i == j - 2)
      {
        localView = getChildAt(paramInt2 - paramInt1);
        if (localView != null) {}
        for (i = localView.getTop();; i = -AndroidUtilities.dp(100.0F))
        {
          if (i >= 0) {
            break label371;
          }
          paramAbsListView.setTag(Integer.valueOf(i));
          break;
        }
        label371:
        paramAbsListView.setTag(Integer.valueOf(0));
      }
      else
      {
        paramAbsListView.setTag(Integer.valueOf(0));
      }
    }
    label393:
    View localView = getChildAt(paramInt2 - paramInt1);
    if (localView != null) {
      paramAbsListView.setTag(Integer.valueOf(localView.getTop()));
    }
    for (;;)
    {
      paramInt2 += j;
      break;
      paramAbsListView.setTag(Integer.valueOf(-AndroidUtilities.dp(100.0F)));
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
    if ((this.mAdapter == null) || (this.headers.isEmpty())) {}
    for (;;)
    {
      return;
      Iterator localIterator = this.headers.iterator();
      while (localIterator.hasNext()) {
        ensurePinnedHeaderLayout((View)localIterator.next(), true);
      }
    }
  }
  
  public void setAdapter(ListAdapter paramListAdapter)
  {
    if (this.mAdapter == paramListAdapter) {
      return;
    }
    this.headers.clear();
    this.headersCache.clear();
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/LetterSectionsListView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */