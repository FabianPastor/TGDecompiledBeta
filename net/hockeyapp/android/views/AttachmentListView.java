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
  private static final String TAG = "AttachmentListView";
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
    return paramLayoutParams instanceof ViewGroup.LayoutParams;
  }
  
  protected ViewGroup.LayoutParams generateDefaultLayoutParams()
  {
    return new ViewGroup.LayoutParams(1, 1);
  }
  
  public ArrayList<Uri> getAttachments()
  {
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    while (i < getChildCount())
    {
      localArrayList.add(((AttachmentView)getChildAt(i)).getAttachmentUri());
      i += 1;
    }
    return localArrayList;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int m = getChildCount();
    paramInt4 = getPaddingLeft();
    paramInt2 = getPaddingTop();
    int j = 0;
    while (j < m)
    {
      View localView = getChildAt(j);
      int k = paramInt4;
      int i = paramInt2;
      if (localView.getVisibility() != 8)
      {
        localView.invalidate();
        int n = localView.getMeasuredWidth();
        int i1 = localView.getMeasuredHeight();
        ViewGroup.LayoutParams localLayoutParams = localView.getLayoutParams();
        k = paramInt4;
        i = paramInt2;
        if (paramInt4 + n > paramInt3 - paramInt1)
        {
          k = getPaddingLeft();
          i = paramInt2 + this.mLineHeight;
        }
        localView.layout(k, i, k + n, i + i1);
        k += localLayoutParams.width + n + ((AttachmentView)localView).getGap();
      }
      j += 1;
      paramInt4 = k;
      paramInt2 = i;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    if (View.MeasureSpec.getMode(paramInt1) == 0) {
      HockeyLog.debug("AttachmentListView", "Width is unspecified");
    }
    int i4 = View.MeasureSpec.getSize(paramInt1);
    int i5 = getChildCount();
    int j = 0;
    int m = 0;
    int i = getPaddingLeft();
    paramInt1 = getPaddingTop();
    int k = 0;
    while (k < i5)
    {
      View localView = getChildAt(k);
      Object localObject = (AttachmentView)localView;
      int i3 = ((AttachmentView)localObject).getEffectiveMaxHeight() + ((AttachmentView)localObject).getPaddingTop();
      int i2 = m;
      int i1 = i;
      int n = paramInt1;
      if (localView.getVisibility() != 8)
      {
        localObject = localView.getLayoutParams();
        localView.measure(View.MeasureSpec.makeMeasureSpec(i4, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(i3, Integer.MIN_VALUE));
        n = localView.getMeasuredWidth();
        i2 = Math.max(m, localView.getMeasuredHeight() + ((ViewGroup.LayoutParams)localObject).height);
        m = i;
        j = paramInt1;
        if (i + n > i4)
        {
          m = getPaddingLeft();
          j = paramInt1 + i2;
        }
        i1 = m + (((ViewGroup.LayoutParams)localObject).width + n);
        n = j;
      }
      k += 1;
      j = i3;
      m = i2;
      i = i1;
      paramInt1 = n;
    }
    this.mLineHeight = m;
    if (View.MeasureSpec.getMode(paramInt2) == 0) {
      i = paramInt1 + m + getPaddingBottom();
    }
    for (;;)
    {
      setMeasuredDimension(i4, i);
      return;
      i = j;
      if (View.MeasureSpec.getMode(paramInt2) == Integer.MIN_VALUE)
      {
        i = j;
        if (paramInt1 + m + getPaddingBottom() < j) {
          i = paramInt1 + m + getPaddingBottom();
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/views/AttachmentListView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */