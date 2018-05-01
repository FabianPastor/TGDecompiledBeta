package net.hockeyapp.android.views;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import java.util.ArrayList;
import net.hockeyapp.android.utils.HockeyLog;

public class AttachmentListView
  extends ViewGroup
{
  private int mLineHeight;
  
  public AttachmentListView(Context paramContext)
  {
    super(paramContext);
  }
  
  public AttachmentListView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    if (paramLayoutParams != null) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  protected ViewGroup.LayoutParams generateDefaultLayoutParams()
  {
    return new ViewGroup.LayoutParams(1, 1);
  }
  
  public ArrayList<Uri> getAttachments()
  {
    ArrayList localArrayList = new ArrayList();
    for (int i = 0; i < getChildCount(); i++) {
      localArrayList.add(((AttachmentView)getChildAt(i)).getAttachmentUri());
    }
    return localArrayList;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = getChildCount();
    paramInt4 = getPaddingLeft();
    paramInt2 = getPaddingTop();
    int j = 0;
    while (j < i)
    {
      View localView = getChildAt(j);
      int k = paramInt4;
      int m = paramInt2;
      if (localView.getVisibility() != 8)
      {
        localView.invalidate();
        int n = localView.getMeasuredWidth();
        int i1 = localView.getMeasuredHeight();
        ViewGroup.LayoutParams localLayoutParams = localView.getLayoutParams();
        k = paramInt4;
        m = paramInt2;
        if (paramInt4 + n > paramInt3 - paramInt1)
        {
          k = getPaddingLeft();
          m = paramInt2 + this.mLineHeight;
        }
        localView.layout(k, m, k + n, m + i1);
        k += localLayoutParams.width + n + ((AttachmentView)localView).getGap();
      }
      j++;
      paramInt4 = k;
      paramInt2 = m;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    if (View.MeasureSpec.getMode(paramInt1) == 0) {
      HockeyLog.debug("AttachmentListView", "Width is unspecified");
    }
    int i = View.MeasureSpec.getSize(paramInt1);
    int j = getChildCount();
    int k = 0;
    int m = 0;
    int n = getPaddingLeft();
    paramInt1 = getPaddingTop();
    int i1 = 0;
    while (i1 < j)
    {
      View localView = getChildAt(i1);
      Object localObject = (AttachmentView)localView;
      int i2 = ((AttachmentView)localObject).getEffectiveMaxHeight() + ((AttachmentView)localObject).getPaddingTop();
      int i3 = m;
      int i4 = n;
      int i5 = paramInt1;
      if (localView.getVisibility() != 8)
      {
        localObject = localView.getLayoutParams();
        localView.measure(View.MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(i2, Integer.MIN_VALUE));
        i5 = localView.getMeasuredWidth();
        i3 = Math.max(m, localView.getMeasuredHeight() + ((ViewGroup.LayoutParams)localObject).height);
        m = n;
        k = paramInt1;
        if (n + i5 > i)
        {
          m = getPaddingLeft();
          k = paramInt1 + i3;
        }
        i4 = m + (((ViewGroup.LayoutParams)localObject).width + i5);
        i5 = k;
      }
      i1++;
      k = i2;
      m = i3;
      n = i4;
      paramInt1 = i5;
    }
    this.mLineHeight = m;
    if (View.MeasureSpec.getMode(paramInt2) == 0) {
      n = paramInt1 + m + getPaddingBottom();
    }
    for (;;)
    {
      setMeasuredDimension(i, n);
      return;
      n = k;
      if (View.MeasureSpec.getMode(paramInt2) == Integer.MIN_VALUE)
      {
        n = k;
        if (paramInt1 + m + getPaddingBottom() < k) {
          n = paramInt1 + m + getPaddingBottom();
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/views/AttachmentListView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */