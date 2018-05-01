package org.telegram.messenger.support.widget;

import android.graphics.PointF;
import android.view.View;

public class LinearSnapHelper
  extends SnapHelper
{
  private static final float INVALID_DISTANCE = 1.0F;
  private OrientationHelper mHorizontalHelper;
  private OrientationHelper mVerticalHelper;
  
  private float computeDistancePerChild(RecyclerView.LayoutManager paramLayoutManager, OrientationHelper paramOrientationHelper)
  {
    Object localObject1 = null;
    Object localObject2 = null;
    int i = Integer.MAX_VALUE;
    int j = Integer.MIN_VALUE;
    int k = paramLayoutManager.getChildCount();
    float f;
    if (k == 0) {
      f = 1.0F;
    }
    for (;;)
    {
      return f;
      int m = 0;
      int i2;
      if (m < k)
      {
        View localView = paramLayoutManager.getChildAt(m);
        int n = paramLayoutManager.getPosition(localView);
        Object localObject3;
        int i1;
        if (n == -1)
        {
          localObject3 = localObject1;
          i1 = i;
          i = j;
        }
        for (;;)
        {
          m++;
          j = i;
          i = i1;
          localObject1 = localObject3;
          break;
          i2 = i;
          if (n < i)
          {
            i2 = n;
            localObject1 = localView;
          }
          i = j;
          i1 = i2;
          localObject3 = localObject1;
          if (n > j)
          {
            i = n;
            localObject2 = localView;
            i1 = i2;
            localObject3 = localObject1;
          }
        }
      }
      if ((localObject1 == null) || (localObject2 == null))
      {
        f = 1.0F;
      }
      else
      {
        i2 = Math.min(paramOrientationHelper.getDecoratedStart((View)localObject1), paramOrientationHelper.getDecoratedStart((View)localObject2));
        i2 = Math.max(paramOrientationHelper.getDecoratedEnd((View)localObject1), paramOrientationHelper.getDecoratedEnd((View)localObject2)) - i2;
        if (i2 == 0) {
          f = 1.0F;
        } else {
          f = 1.0F * i2 / (j - i + 1);
        }
      }
    }
  }
  
  private int distanceToCenter(RecyclerView.LayoutManager paramLayoutManager, View paramView, OrientationHelper paramOrientationHelper)
  {
    int i = paramOrientationHelper.getDecoratedStart(paramView);
    int j = paramOrientationHelper.getDecoratedMeasurement(paramView) / 2;
    if (paramLayoutManager.getClipToPadding()) {}
    for (int k = paramOrientationHelper.getStartAfterPadding() + paramOrientationHelper.getTotalSpace() / 2;; k = paramOrientationHelper.getEnd() / 2) {
      return i + j - k;
    }
  }
  
  private int estimateNextPositionDiffForFling(RecyclerView.LayoutManager paramLayoutManager, OrientationHelper paramOrientationHelper, int paramInt1, int paramInt2)
  {
    int i = 0;
    int[] arrayOfInt = calculateScrollDistance(paramInt1, paramInt2);
    float f = computeDistancePerChild(paramLayoutManager, paramOrientationHelper);
    if (f <= 0.0F)
    {
      paramInt1 = i;
      return paramInt1;
    }
    if (Math.abs(arrayOfInt[0]) > Math.abs(arrayOfInt[1])) {}
    for (paramInt1 = arrayOfInt[0];; paramInt1 = arrayOfInt[1])
    {
      paramInt1 = Math.round(paramInt1 / f);
      break;
    }
  }
  
  private View findCenterView(RecyclerView.LayoutManager paramLayoutManager, OrientationHelper paramOrientationHelper)
  {
    int i = paramLayoutManager.getChildCount();
    Object localObject1;
    if (i == 0)
    {
      localObject1 = null;
      return (View)localObject1;
    }
    Object localObject2 = null;
    if (paramLayoutManager.getClipToPadding()) {}
    for (int j = paramOrientationHelper.getStartAfterPadding() + paramOrientationHelper.getTotalSpace() / 2;; j = paramOrientationHelper.getEnd() / 2)
    {
      int k = Integer.MAX_VALUE;
      int m = 0;
      for (;;)
      {
        localObject1 = localObject2;
        if (m >= i) {
          break;
        }
        localObject1 = paramLayoutManager.getChildAt(m);
        int n = Math.abs(paramOrientationHelper.getDecoratedStart((View)localObject1) + paramOrientationHelper.getDecoratedMeasurement((View)localObject1) / 2 - j);
        int i1 = k;
        if (n < k)
        {
          i1 = n;
          localObject2 = localObject1;
        }
        m++;
        k = i1;
      }
    }
  }
  
  private OrientationHelper getHorizontalHelper(RecyclerView.LayoutManager paramLayoutManager)
  {
    if ((this.mHorizontalHelper == null) || (this.mHorizontalHelper.mLayoutManager != paramLayoutManager)) {
      this.mHorizontalHelper = OrientationHelper.createHorizontalHelper(paramLayoutManager);
    }
    return this.mHorizontalHelper;
  }
  
  private OrientationHelper getVerticalHelper(RecyclerView.LayoutManager paramLayoutManager)
  {
    if ((this.mVerticalHelper == null) || (this.mVerticalHelper.mLayoutManager != paramLayoutManager)) {
      this.mVerticalHelper = OrientationHelper.createVerticalHelper(paramLayoutManager);
    }
    return this.mVerticalHelper;
  }
  
  public int[] calculateDistanceToFinalSnap(RecyclerView.LayoutManager paramLayoutManager, View paramView)
  {
    int[] arrayOfInt = new int[2];
    if (paramLayoutManager.canScrollHorizontally())
    {
      arrayOfInt[0] = distanceToCenter(paramLayoutManager, paramView, getHorizontalHelper(paramLayoutManager));
      if (!paramLayoutManager.canScrollVertically()) {
        break label55;
      }
      arrayOfInt[1] = distanceToCenter(paramLayoutManager, paramView, getVerticalHelper(paramLayoutManager));
    }
    for (;;)
    {
      return arrayOfInt;
      arrayOfInt[0] = 0;
      break;
      label55:
      arrayOfInt[1] = 0;
    }
  }
  
  public View findSnapView(RecyclerView.LayoutManager paramLayoutManager)
  {
    if (paramLayoutManager.canScrollVertically()) {
      paramLayoutManager = findCenterView(paramLayoutManager, getVerticalHelper(paramLayoutManager));
    }
    for (;;)
    {
      return paramLayoutManager;
      if (paramLayoutManager.canScrollHorizontally()) {
        paramLayoutManager = findCenterView(paramLayoutManager, getHorizontalHelper(paramLayoutManager));
      } else {
        paramLayoutManager = null;
      }
    }
  }
  
  public int findTargetSnapPosition(RecyclerView.LayoutManager paramLayoutManager, int paramInt1, int paramInt2)
  {
    int i = -1;
    int j;
    if (!(paramLayoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
      j = i;
    }
    int k;
    Object localObject;
    int m;
    do
    {
      do
      {
        do
        {
          do
          {
            return j;
            k = paramLayoutManager.getItemCount();
            j = i;
          } while (k == 0);
          localObject = findSnapView(paramLayoutManager);
          j = i;
        } while (localObject == null);
        m = paramLayoutManager.getPosition((View)localObject);
        j = i;
      } while (m == -1);
      localObject = ((RecyclerView.SmoothScroller.ScrollVectorProvider)paramLayoutManager).computeScrollVectorForPosition(k - 1);
      j = i;
    } while (localObject == null);
    if (paramLayoutManager.canScrollHorizontally())
    {
      j = estimateNextPositionDiffForFling(paramLayoutManager, getHorizontalHelper(paramLayoutManager), paramInt1, 0);
      paramInt1 = j;
      if (((PointF)localObject).x < 0.0F) {
        paramInt1 = -j;
      }
      label128:
      if (!paramLayoutManager.canScrollVertically()) {
        break label219;
      }
      j = estimateNextPositionDiffForFling(paramLayoutManager, getVerticalHelper(paramLayoutManager), 0, paramInt2);
      paramInt2 = j;
      if (((PointF)localObject).y < 0.0F) {
        paramInt2 = -j;
      }
      label166:
      if (!paramLayoutManager.canScrollVertically()) {
        break label224;
      }
      paramInt1 = paramInt2;
    }
    label219:
    label224:
    for (;;)
    {
      j = i;
      if (paramInt1 == 0) {
        break;
      }
      paramInt2 = m + paramInt1;
      paramInt1 = paramInt2;
      if (paramInt2 < 0) {
        paramInt1 = 0;
      }
      j = paramInt1;
      if (paramInt1 < k) {
        break;
      }
      j = k - 1;
      break;
      paramInt1 = 0;
      break label128;
      paramInt2 = 0;
      break label166;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/support/widget/LinearSnapHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */