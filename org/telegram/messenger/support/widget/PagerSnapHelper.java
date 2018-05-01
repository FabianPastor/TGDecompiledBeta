package org.telegram.messenger.support.widget;

import android.content.Context;
import android.graphics.PointF;
import android.util.DisplayMetrics;
import android.view.View;

public class PagerSnapHelper
  extends SnapHelper
{
  private static final int MAX_SCROLL_ON_FLING_DURATION = 100;
  private OrientationHelper mHorizontalHelper;
  private OrientationHelper mVerticalHelper;
  
  private int distanceToCenter(RecyclerView.LayoutManager paramLayoutManager, View paramView, OrientationHelper paramOrientationHelper)
  {
    int i = paramOrientationHelper.getDecoratedStart(paramView);
    int j = paramOrientationHelper.getDecoratedMeasurement(paramView) / 2;
    if (paramLayoutManager.getClipToPadding()) {}
    for (int k = paramOrientationHelper.getStartAfterPadding() + paramOrientationHelper.getTotalSpace() / 2;; k = paramOrientationHelper.getEnd() / 2) {
      return i + j - k;
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
  
  private View findStartView(RecyclerView.LayoutManager paramLayoutManager, OrientationHelper paramOrientationHelper)
  {
    int i = paramLayoutManager.getChildCount();
    Object localObject1;
    if (i == 0)
    {
      localObject1 = null;
      return (View)localObject1;
    }
    Object localObject2 = null;
    int j = Integer.MAX_VALUE;
    int k = 0;
    for (;;)
    {
      localObject1 = localObject2;
      if (k >= i) {
        break;
      }
      localObject1 = paramLayoutManager.getChildAt(k);
      int m = paramOrientationHelper.getDecoratedStart((View)localObject1);
      int n = j;
      if (m < j)
      {
        n = m;
        localObject2 = localObject1;
      }
      k++;
      j = n;
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
  
  protected LinearSmoothScroller createSnapScroller(RecyclerView.LayoutManager paramLayoutManager)
  {
    if (!(paramLayoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {}
    for (paramLayoutManager = null;; paramLayoutManager = new LinearSmoothScroller(this.mRecyclerView.getContext())
        {
          protected float calculateSpeedPerPixel(DisplayMetrics paramAnonymousDisplayMetrics)
          {
            return 100.0F / paramAnonymousDisplayMetrics.densityDpi;
          }
          
          protected int calculateTimeForScrolling(int paramAnonymousInt)
          {
            return Math.min(100, super.calculateTimeForScrolling(paramAnonymousInt));
          }
          
          protected void onTargetFound(View paramAnonymousView, RecyclerView.State paramAnonymousState, RecyclerView.SmoothScroller.Action paramAnonymousAction)
          {
            paramAnonymousView = PagerSnapHelper.this.calculateDistanceToFinalSnap(PagerSnapHelper.this.mRecyclerView.getLayoutManager(), paramAnonymousView);
            int i = paramAnonymousView[0];
            int j = paramAnonymousView[1];
            int k = calculateTimeForDeceleration(Math.max(Math.abs(i), Math.abs(j)));
            if (k > 0) {
              paramAnonymousAction.update(i, j, k, this.mDecelerateInterpolator);
            }
          }
        }) {
      return paramLayoutManager;
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
    int i = paramLayoutManager.getItemCount();
    if (i == 0) {
      paramInt1 = -1;
    }
    for (;;)
    {
      return paramInt1;
      View localView = null;
      if (paramLayoutManager.canScrollVertically()) {
        localView = findStartView(paramLayoutManager, getVerticalHelper(paramLayoutManager));
      }
      for (;;)
      {
        if (localView != null) {
          break label69;
        }
        paramInt1 = -1;
        break;
        if (paramLayoutManager.canScrollHorizontally()) {
          localView = findStartView(paramLayoutManager, getHorizontalHelper(paramLayoutManager));
        }
      }
      label69:
      int j = paramLayoutManager.getPosition(localView);
      if (j == -1)
      {
        paramInt1 = -1;
      }
      else
      {
        if (paramLayoutManager.canScrollHorizontally()) {
          if (paramInt1 > 0)
          {
            paramInt2 = 1;
            label101:
            int k = 0;
            paramInt1 = k;
            if ((paramLayoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider))
            {
              paramLayoutManager = ((RecyclerView.SmoothScroller.ScrollVectorProvider)paramLayoutManager).computeScrollVectorForPosition(i - 1);
              paramInt1 = k;
              if (paramLayoutManager != null) {
                if ((paramLayoutManager.x >= 0.0F) && (paramLayoutManager.y >= 0.0F)) {
                  break label193;
                }
              }
            }
          }
        }
        label193:
        for (paramInt1 = 1;; paramInt1 = 0)
        {
          if (paramInt1 == 0) {
            break label198;
          }
          paramInt1 = j;
          if (paramInt2 == 0) {
            break;
          }
          paramInt1 = j - 1;
          break;
          paramInt2 = 0;
          break label101;
          if (paramInt2 > 0) {}
          for (paramInt2 = 1;; paramInt2 = 0) {
            break;
          }
        }
        label198:
        paramInt1 = j;
        if (paramInt2 != 0) {
          paramInt1 = j + 1;
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/support/widget/PagerSnapHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */