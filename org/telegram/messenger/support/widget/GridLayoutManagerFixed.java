package org.telegram.messenger.support.widget;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.View.MeasureSpec;
import java.util.ArrayList;
import java.util.Arrays;

public class GridLayoutManagerFixed
  extends GridLayoutManager
{
  private ArrayList<View> additionalViews = new ArrayList(4);
  private boolean canScrollVertically = true;
  
  public GridLayoutManagerFixed(Context paramContext, int paramInt)
  {
    super(paramContext, paramInt);
  }
  
  public GridLayoutManagerFixed(Context paramContext, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    super(paramContext, paramInt1, paramInt2, paramBoolean);
  }
  
  protected int[] calculateItemBorders(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    int[] arrayOfInt;
    if ((paramArrayOfInt != null) && (paramArrayOfInt.length == paramInt1 + 1))
    {
      arrayOfInt = paramArrayOfInt;
      if (paramArrayOfInt[(paramArrayOfInt.length - 1)] == paramInt2) {}
    }
    else
    {
      arrayOfInt = new int[paramInt1 + 1];
    }
    arrayOfInt[0] = 0;
    for (int i = 1; i <= paramInt1; i++) {
      arrayOfInt[i] = ((int)Math.ceil(i / paramInt1 * paramInt2));
    }
    return arrayOfInt;
  }
  
  public boolean canScrollVertically()
  {
    return this.canScrollVertically;
  }
  
  protected boolean hasSiblingChild(int paramInt)
  {
    return false;
  }
  
  void layoutChunk(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, LinearLayoutManager.LayoutState paramLayoutState, LinearLayoutManager.LayoutChunkResult paramLayoutChunkResult)
  {
    int i = this.mOrientationHelper.getModeInOther();
    boolean bool1;
    int j;
    int k;
    if (paramLayoutState.mItemDirection == 1)
    {
      bool1 = true;
      j = 1;
      paramLayoutChunkResult.mConsumed = 0;
      k = paramLayoutState.mCurrentPosition;
      m = j;
      if (paramLayoutState.mLayoutDirection == -1) {
        break label232;
      }
      m = j;
      if (!hasSiblingChild(paramLayoutState.mCurrentPosition)) {
        break label232;
      }
      m = j;
      if (findViewByPosition(paramLayoutState.mCurrentPosition + 1) != null) {
        break label232;
      }
      if (!hasSiblingChild(paramLayoutState.mCurrentPosition + 1)) {
        break label209;
      }
    }
    int n;
    Object localObject1;
    int i1;
    label209:
    for (paramLayoutState.mCurrentPosition += 3;; paramLayoutState.mCurrentPosition += 2)
    {
      n = paramLayoutState.mCurrentPosition;
      for (m = paramLayoutState.mCurrentPosition; m > k; m--)
      {
        localObject1 = paramLayoutState.next(paramRecycler);
        this.additionalViews.add(localObject1);
        if (m != n)
        {
          calculateItemDecorationsForChild((View)localObject1, this.mDecorInsets);
          measureChild((View)localObject1, i, false);
          i1 = this.mOrientationHelper.getDecoratedMeasurement((View)localObject1);
          paramLayoutState.mOffset -= i1;
          paramLayoutState.mAvailable += i1;
        }
      }
      bool1 = false;
      break;
    }
    paramLayoutState.mCurrentPosition = n;
    int m = j;
    for (;;)
    {
      label232:
      int i2;
      int i3;
      if (m != 0)
      {
        n = 0;
        j = 0;
        k = this.mSpanCount;
        if (this.additionalViews.isEmpty()) {
          break label330;
        }
        m = 1;
        i1 = paramLayoutState.mCurrentPosition;
        if ((n < this.mSpanCount) && (paramLayoutState.hasMore(paramState)) && (k > 0))
        {
          i2 = paramLayoutState.mCurrentPosition;
          i3 = getSpanSize(paramRecycler, paramState, i2);
          i1 = k - i3;
          if (i1 >= 0) {
            break label336;
          }
        }
      }
      label330:
      label336:
      int i4;
      label489:
      for (;;)
      {
        if (n != 0) {
          break label491;
        }
        paramLayoutChunkResult.mFinished = true;
        return;
        m = 0;
        break;
        if (!this.additionalViews.isEmpty())
        {
          localObject1 = (View)this.additionalViews.get(0);
          this.additionalViews.remove(0);
          paramLayoutState.mCurrentPosition -= 1;
        }
        for (;;)
        {
          if (localObject1 == null) {
            break label489;
          }
          i3 = j + i3;
          this.mSet[n] = localObject1;
          i4 = n + 1;
          n = i4;
          j = i3;
          k = i1;
          if (paramLayoutState.mLayoutDirection != -1) {
            break;
          }
          n = i4;
          j = i3;
          k = i1;
          if (i1 > 0) {
            break;
          }
          n = i4;
          j = i3;
          k = i1;
          if (!hasSiblingChild(i2)) {
            break;
          }
          m = 1;
          n = i4;
          j = i3;
          k = i1;
          break;
          localObject1 = paramLayoutState.next(paramRecycler);
        }
      }
      label491:
      i1 = 0;
      float f1 = 0.0F;
      assignSpans(paramRecycler, paramState, n, j, bool1);
      k = 0;
      Object localObject2;
      if (k < n)
      {
        localObject2 = this.mSet[k];
        if (paramLayoutState.mScrapList == null) {
          if (bool1) {
            addView((View)localObject2);
          }
        }
        for (;;)
        {
          calculateItemDecorationsForChild((View)localObject2, this.mDecorInsets);
          measureChild((View)localObject2, i, false);
          i3 = this.mOrientationHelper.getDecoratedMeasurement((View)localObject2);
          j = i1;
          if (i3 > i1) {
            j = i3;
          }
          localObject1 = (GridLayoutManager.LayoutParams)((View)localObject2).getLayoutParams();
          float f2 = 1.0F * this.mOrientationHelper.getDecoratedMeasurementInOther((View)localObject2) / ((GridLayoutManager.LayoutParams)localObject1).mSpanSize;
          float f3 = f1;
          if (f2 > f1) {
            f3 = f2;
          }
          k++;
          i1 = j;
          f1 = f3;
          break;
          addView((View)localObject2, 0);
          continue;
          if (bool1) {
            addDisappearingView((View)localObject2);
          } else {
            addDisappearingView((View)localObject2, 0);
          }
        }
      }
      int i5;
      for (j = 0; j < n; j++)
      {
        localObject1 = this.mSet[j];
        if (this.mOrientationHelper.getDecoratedMeasurement((View)localObject1) != i1)
        {
          localObject2 = (GridLayoutManager.LayoutParams)((View)localObject1).getLayoutParams();
          Rect localRect = ((GridLayoutManager.LayoutParams)localObject2).mDecorInsets;
          i5 = localRect.top;
          int i6 = localRect.bottom;
          int i7 = ((GridLayoutManager.LayoutParams)localObject2).topMargin;
          i2 = ((GridLayoutManager.LayoutParams)localObject2).bottomMargin;
          i3 = localRect.left;
          int i8 = localRect.right;
          k = ((GridLayoutManager.LayoutParams)localObject2).leftMargin;
          i4 = ((GridLayoutManager.LayoutParams)localObject2).rightMargin;
          measureChildWithDecorationsAndMargin((View)localObject1, getChildMeasureSpec(this.mCachedBorders[localObject2.mSpanSize], NUM, i3 + i8 + k + i4, ((GridLayoutManager.LayoutParams)localObject2).width, false), View.MeasureSpec.makeMeasureSpec(i1 - (i5 + i6 + i7 + i2), NUM), true);
        }
      }
      boolean bool2 = shouldLayoutChildFromOpositeSide(this.mSet[0]);
      if (((bool2) && (paramLayoutState.mLayoutDirection == -1)) || ((!bool2) && (paramLayoutState.mLayoutDirection == 1)))
      {
        if (paramLayoutState.mLayoutDirection == -1)
        {
          i4 = paramLayoutState.mOffset - paramLayoutChunkResult.mConsumed;
          i3 = i4 - i1;
        }
        for (j = 0;; j = getWidth())
        {
          n--;
          k = j;
          while (n >= 0)
          {
            localObject1 = this.mSet[n];
            localObject2 = (GridLayoutManager.LayoutParams)((View)localObject1).getLayoutParams();
            i2 = this.mOrientationHelper.getDecoratedMeasurementInOther((View)localObject1);
            j = k;
            if (paramLayoutState.mLayoutDirection == 1) {
              j = k - i2;
            }
            layoutDecoratedWithMargins((View)localObject1, j, i3, j + i2, i4);
            k = j;
            if (paramLayoutState.mLayoutDirection == -1) {
              k = j + i2;
            }
            if ((((GridLayoutManager.LayoutParams)localObject2).isItemRemoved()) || (((GridLayoutManager.LayoutParams)localObject2).isItemChanged())) {
              paramLayoutChunkResult.mIgnoreConsumed = true;
            }
            paramLayoutChunkResult.mFocusable |= ((View)localObject1).hasFocusable();
            n--;
          }
          i3 = paramLayoutState.mOffset + paramLayoutChunkResult.mConsumed;
          i4 = i3 + i1;
        }
      }
      if (paramLayoutState.mLayoutDirection == -1)
      {
        i4 = paramLayoutState.mOffset - paramLayoutChunkResult.mConsumed;
        i3 = i4 - i1;
      }
      for (j = getWidth();; j = 0)
      {
        i2 = 0;
        k = j;
        while (i2 < n)
        {
          localObject1 = this.mSet[i2];
          localObject2 = (GridLayoutManager.LayoutParams)((View)localObject1).getLayoutParams();
          i5 = this.mOrientationHelper.getDecoratedMeasurementInOther((View)localObject1);
          j = k;
          if (paramLayoutState.mLayoutDirection == -1) {
            j = k - i5;
          }
          layoutDecoratedWithMargins((View)localObject1, j, i3, j + i5, i4);
          k = j;
          if (paramLayoutState.mLayoutDirection != -1) {
            k = j + i5;
          }
          if ((((GridLayoutManager.LayoutParams)localObject2).isItemRemoved()) || (((GridLayoutManager.LayoutParams)localObject2).isItemChanged())) {
            paramLayoutChunkResult.mIgnoreConsumed = true;
          }
          paramLayoutChunkResult.mFocusable |= ((View)localObject1).hasFocusable();
          i2++;
        }
        i3 = paramLayoutState.mOffset + paramLayoutChunkResult.mConsumed;
        i4 = i3 + i1;
      }
      paramLayoutChunkResult.mConsumed += i1;
      Arrays.fill(this.mSet, null);
    }
  }
  
  protected void measureChild(View paramView, int paramInt, boolean paramBoolean)
  {
    GridLayoutManager.LayoutParams localLayoutParams = (GridLayoutManager.LayoutParams)paramView.getLayoutParams();
    Rect localRect = localLayoutParams.mDecorInsets;
    int i = localRect.top;
    int j = localRect.bottom;
    int k = localLayoutParams.topMargin;
    int m = localLayoutParams.bottomMargin;
    int n = localRect.left;
    int i1 = localRect.right;
    int i2 = localLayoutParams.leftMargin;
    int i3 = localLayoutParams.rightMargin;
    measureChildWithDecorationsAndMargin(paramView, getChildMeasureSpec(this.mCachedBorders[localLayoutParams.mSpanSize], paramInt, n + i1 + i2 + i3, localLayoutParams.width, false), getChildMeasureSpec(this.mOrientationHelper.getTotalSpace(), getHeightMode(), i + j + k + m, localLayoutParams.height, true), paramBoolean);
  }
  
  protected void recycleViewsFromStart(RecyclerView.Recycler paramRecycler, int paramInt)
  {
    if (paramInt < 0) {}
    label92:
    label152:
    for (;;)
    {
      return;
      int i = getChildCount();
      int j;
      Object localObject;
      if (this.mShouldReverseLayout) {
        for (j = i - 1;; j--)
        {
          if (j < 0) {
            break label92;
          }
          View localView = getChildAt(j);
          localObject = (RecyclerView.LayoutParams)localView.getLayoutParams();
          if ((localView.getBottom() + ((RecyclerView.LayoutParams)localObject).bottomMargin > paramInt) || (localView.getTop() + localView.getHeight() > paramInt))
          {
            recycleChildren(paramRecycler, i - 1, j);
            break;
          }
        }
      } else {
        for (j = 0;; j++)
        {
          if (j >= i) {
            break label152;
          }
          localObject = getChildAt(j);
          if ((this.mOrientationHelper.getDecoratedEnd((View)localObject) > paramInt) || (this.mOrientationHelper.getTransformedEndWithDecoration((View)localObject) > paramInt))
          {
            recycleChildren(paramRecycler, 0, j);
            break;
          }
        }
      }
    }
  }
  
  public void setCanScrollVertically(boolean paramBoolean)
  {
    this.canScrollVertically = paramBoolean;
  }
  
  public boolean shouldLayoutChildFromOpositeSide(View paramView)
  {
    return false;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/support/widget/GridLayoutManagerFixed.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */