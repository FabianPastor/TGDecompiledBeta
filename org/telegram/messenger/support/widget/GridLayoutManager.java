package org.telegram.messenger.support.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.CollectionItemInfoCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import java.util.Arrays;

public class GridLayoutManager
  extends LinearLayoutManager
{
  private static final boolean DEBUG = false;
  public static final int DEFAULT_SPAN_COUNT = -1;
  private static final String TAG = "GridLayoutManager";
  int[] mCachedBorders;
  final Rect mDecorInsets = new Rect();
  boolean mPendingSpanCountChange = false;
  final SparseIntArray mPreLayoutSpanIndexCache = new SparseIntArray();
  final SparseIntArray mPreLayoutSpanSizeCache = new SparseIntArray();
  View[] mSet;
  int mSpanCount = -1;
  SpanSizeLookup mSpanSizeLookup = new DefaultSpanSizeLookup();
  
  public GridLayoutManager(Context paramContext, int paramInt)
  {
    super(paramContext);
    setSpanCount(paramInt);
  }
  
  public GridLayoutManager(Context paramContext, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    super(paramContext, paramInt2, paramBoolean);
    setSpanCount(paramInt1);
  }
  
  private void cachePreLayoutSpanMapping()
  {
    int i = getChildCount();
    for (int j = 0; j < i; j++)
    {
      LayoutParams localLayoutParams = (LayoutParams)getChildAt(j).getLayoutParams();
      int k = localLayoutParams.getViewLayoutPosition();
      this.mPreLayoutSpanSizeCache.put(k, localLayoutParams.getSpanSize());
      this.mPreLayoutSpanIndexCache.put(k, localLayoutParams.getSpanIndex());
    }
  }
  
  private void clearPreLayoutSpanMappingCache()
  {
    this.mPreLayoutSpanSizeCache.clear();
    this.mPreLayoutSpanIndexCache.clear();
  }
  
  private void ensureAnchorIsInCorrectSpan(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, LinearLayoutManager.AnchorInfo paramAnchorInfo, int paramInt)
  {
    int i = 1;
    if (paramInt == 1) {}
    for (paramInt = i;; paramInt = 0)
    {
      i = getSpanIndex(paramRecycler, paramState, paramAnchorInfo.mPosition);
      if (paramInt == 0) {
        break;
      }
      while ((i > 0) && (paramAnchorInfo.mPosition > 0))
      {
        paramAnchorInfo.mPosition -= 1;
        i = getSpanIndex(paramRecycler, paramState, paramAnchorInfo.mPosition);
      }
    }
    int j = paramState.getItemCount();
    paramInt = paramAnchorInfo.mPosition;
    while (paramInt < j - 1)
    {
      int k = getSpanIndex(paramRecycler, paramState, paramInt + 1);
      if (k <= i) {
        break;
      }
      paramInt++;
      i = k;
    }
    paramAnchorInfo.mPosition = paramInt;
  }
  
  private void ensureViewSet()
  {
    if ((this.mSet == null) || (this.mSet.length != this.mSpanCount)) {
      this.mSet = new View[this.mSpanCount];
    }
  }
  
  private int getSpanGroupIndex(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, int paramInt)
  {
    if (!paramState.isPreLayout()) {
      paramInt = this.mSpanSizeLookup.getSpanGroupIndex(paramInt, this.mSpanCount);
    }
    for (;;)
    {
      return paramInt;
      int i = paramRecycler.convertPreLayoutPositionToPostLayout(paramInt);
      if (i == -1)
      {
        Log.w("GridLayoutManager", "Cannot find span size for pre layout position. " + paramInt);
        paramInt = 0;
      }
      else
      {
        paramInt = this.mSpanSizeLookup.getSpanGroupIndex(i, this.mSpanCount);
      }
    }
  }
  
  private int getSpanIndex(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, int paramInt)
  {
    int i;
    if (!paramState.isPreLayout()) {
      i = this.mSpanSizeLookup.getCachedSpanIndex(paramInt, this.mSpanCount);
    }
    for (;;)
    {
      return i;
      int j = this.mPreLayoutSpanIndexCache.get(paramInt, -1);
      i = j;
      if (j == -1)
      {
        i = paramRecycler.convertPreLayoutPositionToPostLayout(paramInt);
        if (i == -1)
        {
          Log.w("GridLayoutManager", "Cannot find span size for pre layout position. It is not cached, not in the adapter. Pos:" + paramInt);
          i = 0;
        }
        else
        {
          i = this.mSpanSizeLookup.getCachedSpanIndex(i, this.mSpanCount);
        }
      }
    }
  }
  
  private void guessMeasurement(float paramFloat, int paramInt)
  {
    calculateItemBorders(Math.max(Math.round(this.mSpanCount * paramFloat), paramInt));
  }
  
  private void updateMeasurements()
  {
    if (getOrientation() == 1) {}
    for (int i = getWidth() - getPaddingRight() - getPaddingLeft();; i = getHeight() - getPaddingBottom() - getPaddingTop())
    {
      calculateItemBorders(i);
      return;
    }
  }
  
  protected void assignSpans(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int i;
    int j;
    if (paramBoolean)
    {
      i = 0;
      j = paramInt1;
      paramInt2 = 1;
      paramInt1 = i;
    }
    for (;;)
    {
      int k = 0;
      i = paramInt1;
      paramInt1 = k;
      while (i != j)
      {
        View localView = this.mSet[i];
        LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
        localLayoutParams.mSpanSize = getSpanSize(paramRecycler, paramState, getPosition(localView));
        localLayoutParams.mSpanIndex = paramInt1;
        paramInt1 += localLayoutParams.mSpanSize;
        i += paramInt2;
      }
      paramInt1--;
      j = -1;
      paramInt2 = -1;
    }
  }
  
  protected void calculateItemBorders(int paramInt)
  {
    this.mCachedBorders = calculateItemBorders(this.mCachedBorders, this.mSpanCount, paramInt);
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
    int i = paramInt2 / paramInt1;
    int j = paramInt2 % paramInt1;
    int k = 0;
    paramInt2 = 0;
    for (int m = 1; m <= paramInt1; m++)
    {
      int n = i;
      int i1 = paramInt2 + j;
      paramInt2 = i1;
      int i2 = n;
      if (i1 > 0)
      {
        paramInt2 = i1;
        i2 = n;
        if (paramInt1 - i1 < j)
        {
          i2 = n + 1;
          paramInt2 = i1 - paramInt1;
        }
      }
      k += i2;
      arrayOfInt[m] = k;
    }
    return arrayOfInt;
  }
  
  public boolean checkLayoutParams(RecyclerView.LayoutParams paramLayoutParams)
  {
    return paramLayoutParams instanceof LayoutParams;
  }
  
  void collectPrefetchPositionsForLayoutState(RecyclerView.State paramState, LinearLayoutManager.LayoutState paramLayoutState, RecyclerView.LayoutManager.LayoutPrefetchRegistry paramLayoutPrefetchRegistry)
  {
    int i = this.mSpanCount;
    for (int j = 0; (j < this.mSpanCount) && (paramLayoutState.hasMore(paramState)) && (i > 0); j++)
    {
      int k = paramLayoutState.mCurrentPosition;
      paramLayoutPrefetchRegistry.addPosition(k, Math.max(0, paramLayoutState.mScrollingOffset));
      i -= this.mSpanSizeLookup.getSpanSize(k);
      paramLayoutState.mCurrentPosition += paramLayoutState.mItemDirection;
    }
  }
  
  View findReferenceChild(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, int paramInt1, int paramInt2, int paramInt3)
  {
    ensureLayoutState();
    Object localObject1 = null;
    Object localObject2 = null;
    int i = this.mOrientationHelper.getStartAfterPadding();
    int j = this.mOrientationHelper.getEndAfterPadding();
    int k;
    View localView;
    Object localObject3;
    Object localObject4;
    if (paramInt2 > paramInt1)
    {
      k = 1;
      if (paramInt1 == paramInt2) {
        break label221;
      }
      localView = getChildAt(paramInt1);
      int m = getPosition(localView);
      localObject3 = localObject1;
      localObject4 = localObject2;
      if (m >= 0)
      {
        localObject3 = localObject1;
        localObject4 = localObject2;
        if (m < paramInt3)
        {
          if (getSpanIndex(paramRecycler, paramState, m) == 0) {
            break label127;
          }
          localObject4 = localObject2;
          localObject3 = localObject1;
        }
      }
    }
    for (;;)
    {
      paramInt1 += k;
      localObject1 = localObject3;
      localObject2 = localObject4;
      break;
      k = -1;
      break;
      label127:
      if (((RecyclerView.LayoutParams)localView.getLayoutParams()).isItemRemoved())
      {
        localObject3 = localObject1;
        localObject4 = localObject2;
        if (localObject1 == null)
        {
          localObject3 = localView;
          localObject4 = localObject2;
        }
      }
      else
      {
        if (this.mOrientationHelper.getDecoratedStart(localView) < j)
        {
          localObject3 = localView;
          if (this.mOrientationHelper.getDecoratedEnd(localView) >= i) {
            break label230;
          }
        }
        localObject3 = localObject1;
        localObject4 = localObject2;
        if (localObject2 == null)
        {
          localObject3 = localObject1;
          localObject4 = localView;
        }
      }
    }
    label221:
    if (localObject2 != null) {}
    for (;;)
    {
      localObject3 = localObject2;
      label230:
      return (View)localObject3;
      localObject2 = localObject1;
    }
  }
  
  public RecyclerView.LayoutParams generateDefaultLayoutParams()
  {
    if (this.mOrientation == 0) {}
    for (LayoutParams localLayoutParams = new LayoutParams(-2, -1);; localLayoutParams = new LayoutParams(-1, -2)) {
      return localLayoutParams;
    }
  }
  
  public RecyclerView.LayoutParams generateLayoutParams(Context paramContext, AttributeSet paramAttributeSet)
  {
    return new LayoutParams(paramContext, paramAttributeSet);
  }
  
  public RecyclerView.LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    if ((paramLayoutParams instanceof ViewGroup.MarginLayoutParams)) {}
    for (paramLayoutParams = new LayoutParams((ViewGroup.MarginLayoutParams)paramLayoutParams);; paramLayoutParams = new LayoutParams(paramLayoutParams)) {
      return paramLayoutParams;
    }
  }
  
  public int getColumnCountForAccessibility(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    int i;
    if (this.mOrientation == 1) {
      i = this.mSpanCount;
    }
    for (;;)
    {
      return i;
      if (paramState.getItemCount() < 1) {
        i = 0;
      } else {
        i = getSpanGroupIndex(paramRecycler, paramState, paramState.getItemCount() - 1) + 1;
      }
    }
  }
  
  public int getRowCountForAccessibility(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    int i;
    if (this.mOrientation == 0) {
      i = this.mSpanCount;
    }
    for (;;)
    {
      return i;
      if (paramState.getItemCount() < 1) {
        i = 0;
      } else {
        i = getSpanGroupIndex(paramRecycler, paramState, paramState.getItemCount() - 1) + 1;
      }
    }
  }
  
  int getSpaceForSpanRange(int paramInt1, int paramInt2)
  {
    if ((this.mOrientation == 1) && (isLayoutRTL())) {}
    for (paramInt1 = this.mCachedBorders[(this.mSpanCount - paramInt1)] - this.mCachedBorders[(this.mSpanCount - paramInt1 - paramInt2)];; paramInt1 = this.mCachedBorders[(paramInt1 + paramInt2)] - this.mCachedBorders[paramInt1]) {
      return paramInt1;
    }
  }
  
  public int getSpanCount()
  {
    return this.mSpanCount;
  }
  
  protected int getSpanSize(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, int paramInt)
  {
    int i;
    if (!paramState.isPreLayout()) {
      i = this.mSpanSizeLookup.getSpanSize(paramInt);
    }
    for (;;)
    {
      return i;
      int j = this.mPreLayoutSpanSizeCache.get(paramInt, -1);
      i = j;
      if (j == -1)
      {
        i = paramRecycler.convertPreLayoutPositionToPostLayout(paramInt);
        if (i == -1)
        {
          Log.w("GridLayoutManager", "Cannot find span size for pre layout position. It is not cached, not in the adapter. Pos:" + paramInt);
          i = 1;
        }
        else
        {
          i = this.mSpanSizeLookup.getSpanSize(i);
        }
      }
    }
  }
  
  public SpanSizeLookup getSpanSizeLookup()
  {
    return this.mSpanSizeLookup;
  }
  
  void layoutChunk(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, LinearLayoutManager.LayoutState paramLayoutState, LinearLayoutManager.LayoutChunkResult paramLayoutChunkResult)
  {
    int i = this.mOrientationHelper.getModeInOther();
    int j;
    int k;
    if (i != NUM)
    {
      j = 1;
      if (getChildCount() <= 0) {
        break label226;
      }
      k = this.mCachedBorders[this.mSpanCount];
      label38:
      if (j != 0) {
        updateMeasurements();
      }
      if (paramLayoutState.mItemDirection != 1) {
        break label232;
      }
    }
    int m;
    int n;
    int i1;
    int i2;
    int i3;
    label226:
    label232:
    for (boolean bool = true;; bool = false)
    {
      m = 0;
      n = 0;
      i1 = this.mSpanCount;
      i2 = m;
      i3 = n;
      if (!bool)
      {
        i1 = getSpanIndex(paramRecycler, paramState, paramLayoutState.mCurrentPosition) + getSpanSize(paramRecycler, paramState, paramLayoutState.mCurrentPosition);
        i3 = n;
        i2 = m;
      }
      if ((i2 >= this.mSpanCount) || (!paramLayoutState.hasMore(paramState)) || (i1 <= 0)) {
        break label250;
      }
      m = paramLayoutState.mCurrentPosition;
      n = getSpanSize(paramRecycler, paramState, m);
      if (n <= this.mSpanCount) {
        break label238;
      }
      throw new IllegalArgumentException("Item at position " + m + " requires " + n + " spans but GridLayoutManager has only " + this.mSpanCount + " spans.");
      j = 0;
      break;
      k = 0;
      break label38;
    }
    label238:
    i1 -= n;
    if (i1 < 0)
    {
      label250:
      if (i2 != 0) {
        break label296;
      }
      paramLayoutChunkResult.mFinished = true;
    }
    for (;;)
    {
      return;
      View localView = paramLayoutState.next(paramRecycler);
      if (localView == null) {
        break label250;
      }
      i3 += n;
      this.mSet[i2] = localView;
      i2++;
      break;
      label296:
      i1 = 0;
      float f1 = 0.0F;
      assignSpans(paramRecycler, paramState, i2, i3, bool);
      i3 = 0;
      if (i3 < i2)
      {
        paramState = this.mSet[i3];
        if (paramLayoutState.mScrapList == null) {
          if (bool) {
            addView(paramState);
          }
        }
        for (;;)
        {
          calculateItemDecorationsForChild(paramState, this.mDecorInsets);
          measureChild(paramState, i, false);
          m = this.mOrientationHelper.getDecoratedMeasurement(paramState);
          n = i1;
          if (m > i1) {
            n = m;
          }
          paramRecycler = (LayoutParams)paramState.getLayoutParams();
          float f2 = 1.0F * this.mOrientationHelper.getDecoratedMeasurementInOther(paramState) / paramRecycler.mSpanSize;
          float f3 = f1;
          if (f2 > f1) {
            f3 = f2;
          }
          i3++;
          i1 = n;
          f1 = f3;
          break;
          addView(paramState, 0);
          continue;
          if (bool) {
            addDisappearingView(paramState);
          } else {
            addDisappearingView(paramState, 0);
          }
        }
      }
      i3 = i1;
      if (j != 0)
      {
        guessMeasurement(f1, k);
        i1 = 0;
        j = 0;
        for (;;)
        {
          i3 = i1;
          if (j >= i2) {
            break;
          }
          paramRecycler = this.mSet[j];
          measureChild(paramRecycler, NUM, true);
          k = this.mOrientationHelper.getDecoratedMeasurement(paramRecycler);
          i3 = i1;
          if (k > i1) {
            i3 = k;
          }
          j++;
          i1 = i3;
        }
      }
      i1 = 0;
      if (i1 < i2)
      {
        localView = this.mSet[i1];
        if (this.mOrientationHelper.getDecoratedMeasurement(localView) != i3)
        {
          paramState = (LayoutParams)localView.getLayoutParams();
          paramRecycler = paramState.mDecorInsets;
          j = paramRecycler.top + paramRecycler.bottom + paramState.topMargin + paramState.bottomMargin;
          k = paramRecycler.left + paramRecycler.right + paramState.leftMargin + paramState.rightMargin;
          n = getSpaceForSpanRange(paramState.mSpanIndex, paramState.mSpanSize);
          if (this.mOrientation != 1) {
            break label723;
          }
          k = getChildMeasureSpec(n, NUM, k, paramState.width, false);
        }
        for (j = View.MeasureSpec.makeMeasureSpec(i3 - j, NUM);; j = getChildMeasureSpec(n, NUM, j, paramState.height, false))
        {
          measureChildWithDecorationsAndMargin(localView, k, j, true);
          i1++;
          break;
          label723:
          k = View.MeasureSpec.makeMeasureSpec(i3 - k, NUM);
        }
      }
      paramLayoutChunkResult.mConsumed = i3;
      i1 = 0;
      n = 0;
      j = 0;
      k = 0;
      if (this.mOrientation == 1) {
        if (paramLayoutState.mLayoutDirection == -1)
        {
          k = paramLayoutState.mOffset;
          j = k - i3;
          i3 = n;
          n = 0;
          m = i3;
          i3 = j;
          label819:
          if (n >= i2) {
            break label1081;
          }
          paramState = this.mSet[n];
          paramRecycler = (LayoutParams)paramState.getLayoutParams();
          if (this.mOrientation != 1) {
            break label1045;
          }
          if (!isLayoutRTL()) {
            break label1013;
          }
          j = getPaddingLeft() + this.mCachedBorders[(this.mSpanCount - paramRecycler.mSpanIndex)];
          i1 = j - this.mOrientationHelper.getDecoratedMeasurementInOther(paramState);
        }
      }
      for (;;)
      {
        layoutDecoratedWithMargins(paramState, i1, i3, j, k);
        if ((paramRecycler.isItemRemoved()) || (paramRecycler.isItemChanged())) {
          paramLayoutChunkResult.mIgnoreConsumed = true;
        }
        paramLayoutChunkResult.mFocusable |= paramState.hasFocusable();
        n++;
        m = j;
        break label819;
        j = paramLayoutState.mOffset;
        k = j + i3;
        i3 = n;
        break;
        if (paramLayoutState.mLayoutDirection == -1)
        {
          n = paramLayoutState.mOffset;
          i1 = n - i3;
          i3 = n;
          break;
        }
        i1 = paramLayoutState.mOffset;
        i3 = i1 + i3;
        break;
        label1013:
        i1 = getPaddingLeft() + this.mCachedBorders[paramRecycler.mSpanIndex];
        j = i1 + this.mOrientationHelper.getDecoratedMeasurementInOther(paramState);
        continue;
        label1045:
        i3 = getPaddingTop() + this.mCachedBorders[paramRecycler.mSpanIndex];
        k = i3 + this.mOrientationHelper.getDecoratedMeasurementInOther(paramState);
        j = m;
      }
      label1081:
      Arrays.fill(this.mSet, null);
    }
  }
  
  protected void measureChild(View paramView, int paramInt, boolean paramBoolean)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    Rect localRect = localLayoutParams.mDecorInsets;
    int i = localRect.top + localRect.bottom + localLayoutParams.topMargin + localLayoutParams.bottomMargin;
    int j = localRect.left + localRect.right + localLayoutParams.leftMargin + localLayoutParams.rightMargin;
    int k = getSpaceForSpanRange(localLayoutParams.mSpanIndex, localLayoutParams.mSpanSize);
    if (this.mOrientation == 1)
    {
      j = getChildMeasureSpec(k, paramInt, j, localLayoutParams.width, false);
      paramInt = getChildMeasureSpec(this.mOrientationHelper.getTotalSpace(), getHeightMode(), i, localLayoutParams.height, true);
    }
    for (;;)
    {
      measureChildWithDecorationsAndMargin(paramView, j, paramInt, paramBoolean);
      return;
      paramInt = getChildMeasureSpec(k, paramInt, i, localLayoutParams.height, false);
      j = getChildMeasureSpec(this.mOrientationHelper.getTotalSpace(), getWidthMode(), j, localLayoutParams.width, true);
    }
  }
  
  protected void measureChildWithDecorationsAndMargin(View paramView, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    RecyclerView.LayoutParams localLayoutParams = (RecyclerView.LayoutParams)paramView.getLayoutParams();
    if (paramBoolean) {}
    for (paramBoolean = shouldReMeasureChild(paramView, paramInt1, paramInt2, localLayoutParams);; paramBoolean = shouldMeasureChild(paramView, paramInt1, paramInt2, localLayoutParams))
    {
      if (paramBoolean) {
        paramView.measure(paramInt1, paramInt2);
      }
      return;
    }
  }
  
  void onAnchorReady(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, LinearLayoutManager.AnchorInfo paramAnchorInfo, int paramInt)
  {
    super.onAnchorReady(paramRecycler, paramState, paramAnchorInfo, paramInt);
    updateMeasurements();
    if ((paramState.getItemCount() > 0) && (!paramState.isPreLayout())) {
      ensureAnchorIsInCorrectSpan(paramRecycler, paramState, paramAnchorInfo, paramInt);
    }
    ensureViewSet();
  }
  
  public View onFocusSearchFailed(View paramView, int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    View localView1 = findContainingItemView(paramView);
    if (localView1 == null) {}
    Object localObject1;
    int i;
    int j;
    for (paramView = null;; paramView = null)
    {
      return paramView;
      localObject1 = (LayoutParams)localView1.getLayoutParams();
      i = ((LayoutParams)localObject1).mSpanIndex;
      j = ((LayoutParams)localObject1).mSpanIndex + ((LayoutParams)localObject1).mSpanSize;
      if (super.onFocusSearchFailed(paramView, paramInt, paramRecycler, paramState) != null) {
        break;
      }
    }
    int k;
    label75:
    label86:
    int m;
    int n;
    label103:
    int i1;
    label121:
    Object localObject2;
    int i2;
    int i3;
    int i4;
    int i5;
    int i6;
    int i7;
    label152:
    View localView2;
    if (convertFocusDirectionToLayoutDirection(paramInt) == 1)
    {
      k = 1;
      if (k == this.mShouldReverseLayout) {
        break label201;
      }
      paramInt = 1;
      if (paramInt == 0) {
        break label206;
      }
      paramInt = getChildCount() - 1;
      m = -1;
      n = -1;
      if ((this.mOrientation != 1) || (!isLayoutRTL())) {
        break label220;
      }
      i1 = 1;
      localObject2 = null;
      i2 = -1;
      i3 = 0;
      localObject1 = null;
      i4 = -1;
      i5 = 0;
      i6 = getSpanGroupIndex(paramRecycler, paramState, paramInt);
      i7 = paramInt;
      if (i7 != n)
      {
        paramInt = getSpanGroupIndex(paramRecycler, paramState, i7);
        localView2 = getChildAt(i7);
        if (localView2 != localView1) {
          break label226;
        }
      }
      label184:
      if (localObject2 == null) {
        break label661;
      }
    }
    for (;;)
    {
      paramView = (View)localObject2;
      break;
      k = 0;
      break label75;
      label201:
      paramInt = 0;
      break label86;
      label206:
      paramInt = 0;
      m = 1;
      n = getChildCount();
      break label103;
      label220:
      i1 = 0;
      break label121;
      label226:
      int i8;
      int i9;
      Object localObject3;
      int i10;
      int i11;
      if ((localView2.hasFocusable()) && (paramInt != i6))
      {
        if (localObject2 != null) {
          break label184;
        }
        i8 = i4;
        i9 = i5;
        localObject3 = localObject1;
        i10 = i2;
        i11 = i3;
        paramView = (View)localObject2;
      }
      for (;;)
      {
        i7 += m;
        localObject2 = paramView;
        i3 = i11;
        i2 = i10;
        localObject1 = localObject3;
        i5 = i9;
        i4 = i8;
        break label152;
        LayoutParams localLayoutParams = (LayoutParams)localView2.getLayoutParams();
        int i12 = localLayoutParams.mSpanIndex;
        int i13 = localLayoutParams.mSpanIndex + localLayoutParams.mSpanSize;
        if ((localView2.hasFocusable()) && (i12 == i))
        {
          paramView = localView2;
          if (i13 == j) {
            break;
          }
        }
        i10 = 0;
        if (((localView2.hasFocusable()) && (localObject2 == null)) || ((!localView2.hasFocusable()) && (localObject1 == null))) {
          paramInt = 1;
        }
        label578:
        do
        {
          for (;;)
          {
            paramView = (View)localObject2;
            i11 = i3;
            i10 = i2;
            localObject3 = localObject1;
            i9 = i5;
            i8 = i4;
            if (paramInt == 0) {
              break;
            }
            if (!localView2.hasFocusable()) {
              break label619;
            }
            paramView = localView2;
            i10 = localLayoutParams.mSpanIndex;
            i11 = Math.min(i13, j) - Math.max(i12, i);
            localObject3 = localObject1;
            i9 = i5;
            i8 = i4;
            break;
            paramInt = Math.max(i12, i);
            i11 = Math.min(i13, j) - paramInt;
            if (localView2.hasFocusable())
            {
              if (i11 > i3)
              {
                paramInt = 1;
              }
              else
              {
                paramInt = i10;
                if (i11 == i3)
                {
                  if (i12 > i2) {}
                  for (i11 = 1;; i11 = 0)
                  {
                    paramInt = i10;
                    if (i1 != i11) {
                      break;
                    }
                    paramInt = 1;
                    break;
                  }
                }
              }
            }
            else
            {
              paramInt = i10;
              if (localObject2 == null)
              {
                paramInt = i10;
                if (isViewPartiallyVisible(localView2, false, true))
                {
                  if (i11 <= i5) {
                    break label578;
                  }
                  paramInt = 1;
                }
              }
            }
          }
          paramInt = i10;
        } while (i11 != i5);
        if (i12 > i4) {}
        for (i11 = 1;; i11 = 0)
        {
          paramInt = i10;
          if (i1 != i11) {
            break;
          }
          paramInt = 1;
          break;
        }
        label619:
        i8 = localLayoutParams.mSpanIndex;
        i9 = Math.min(i13, j) - Math.max(i12, i);
        paramView = (View)localObject2;
        i11 = i3;
        i10 = i2;
        localObject3 = localView2;
      }
      label661:
      localObject2 = localObject1;
    }
  }
  
  public void onInitializeAccessibilityNodeInfoForItem(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
  {
    ViewGroup.LayoutParams localLayoutParams = paramView.getLayoutParams();
    if (!(localLayoutParams instanceof LayoutParams))
    {
      super.onInitializeAccessibilityNodeInfoForItem(paramView, paramAccessibilityNodeInfoCompat);
      return;
    }
    paramView = (LayoutParams)localLayoutParams;
    int i = getSpanGroupIndex(paramRecycler, paramState, paramView.getViewLayoutPosition());
    if (this.mOrientation == 0)
    {
      j = paramView.getSpanIndex();
      k = paramView.getSpanSize();
      if ((this.mSpanCount > 1) && (paramView.getSpanSize() == this.mSpanCount)) {}
      for (bool = true;; bool = false)
      {
        paramAccessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(j, k, i, 1, bool, false));
        break;
      }
    }
    int k = paramView.getSpanIndex();
    int j = paramView.getSpanSize();
    if ((this.mSpanCount > 1) && (paramView.getSpanSize() == this.mSpanCount)) {}
    for (boolean bool = true;; bool = false)
    {
      paramAccessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(i, 1, k, j, bool, false));
      break;
    }
  }
  
  public void onItemsAdded(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
  {
    this.mSpanSizeLookup.invalidateSpanIndexCache();
  }
  
  public void onItemsChanged(RecyclerView paramRecyclerView)
  {
    this.mSpanSizeLookup.invalidateSpanIndexCache();
  }
  
  public void onItemsMoved(RecyclerView paramRecyclerView, int paramInt1, int paramInt2, int paramInt3)
  {
    this.mSpanSizeLookup.invalidateSpanIndexCache();
  }
  
  public void onItemsRemoved(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
  {
    this.mSpanSizeLookup.invalidateSpanIndexCache();
  }
  
  public void onItemsUpdated(RecyclerView paramRecyclerView, int paramInt1, int paramInt2, Object paramObject)
  {
    this.mSpanSizeLookup.invalidateSpanIndexCache();
  }
  
  public void onLayoutChildren(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    if (paramState.isPreLayout()) {
      cachePreLayoutSpanMapping();
    }
    super.onLayoutChildren(paramRecycler, paramState);
    clearPreLayoutSpanMappingCache();
  }
  
  public void onLayoutCompleted(RecyclerView.State paramState)
  {
    super.onLayoutCompleted(paramState);
    this.mPendingSpanCountChange = false;
  }
  
  public int scrollHorizontallyBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    updateMeasurements();
    ensureViewSet();
    return super.scrollHorizontallyBy(paramInt, paramRecycler, paramState);
  }
  
  public int scrollVerticallyBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    updateMeasurements();
    ensureViewSet();
    return super.scrollVerticallyBy(paramInt, paramRecycler, paramState);
  }
  
  public void setMeasuredDimension(Rect paramRect, int paramInt1, int paramInt2)
  {
    if (this.mCachedBorders == null) {
      super.setMeasuredDimension(paramRect, paramInt1, paramInt2);
    }
    int i = getPaddingLeft() + getPaddingRight();
    int j = getPaddingTop() + getPaddingBottom();
    int k;
    if (this.mOrientation == 1)
    {
      k = chooseSize(paramInt2, paramRect.height() + j, getMinimumHeight());
      paramInt2 = chooseSize(paramInt1, this.mCachedBorders[(this.mCachedBorders.length - 1)] + i, getMinimumWidth());
      paramInt1 = k;
    }
    for (;;)
    {
      setMeasuredDimension(paramInt2, paramInt1);
      return;
      k = chooseSize(paramInt1, paramRect.width() + i, getMinimumWidth());
      paramInt1 = chooseSize(paramInt2, this.mCachedBorders[(this.mCachedBorders.length - 1)] + j, getMinimumHeight());
      paramInt2 = k;
    }
  }
  
  public void setSpanCount(int paramInt)
  {
    if (paramInt == this.mSpanCount) {}
    for (;;)
    {
      return;
      this.mPendingSpanCountChange = true;
      if (paramInt < 1) {
        throw new IllegalArgumentException("Span count should be at least 1. Provided " + paramInt);
      }
      this.mSpanCount = paramInt;
      this.mSpanSizeLookup.invalidateSpanIndexCache();
      requestLayout();
    }
  }
  
  public void setSpanSizeLookup(SpanSizeLookup paramSpanSizeLookup)
  {
    this.mSpanSizeLookup = paramSpanSizeLookup;
  }
  
  public void setStackFromEnd(boolean paramBoolean)
  {
    if (paramBoolean) {
      throw new UnsupportedOperationException("GridLayoutManager does not support stack from end. Consider using reverse layout");
    }
    super.setStackFromEnd(false);
  }
  
  public boolean supportsPredictiveItemAnimations()
  {
    if ((this.mPendingSavedState == null) && (!this.mPendingSpanCountChange)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static final class DefaultSpanSizeLookup
    extends GridLayoutManager.SpanSizeLookup
  {
    public int getSpanIndex(int paramInt1, int paramInt2)
    {
      return paramInt1 % paramInt2;
    }
    
    public int getSpanSize(int paramInt)
    {
      return 1;
    }
  }
  
  public static class LayoutParams
    extends RecyclerView.LayoutParams
  {
    public static final int INVALID_SPAN_ID = -1;
    int mSpanIndex = -1;
    int mSpanSize = 0;
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
    }
    
    public LayoutParams(ViewGroup.LayoutParams paramLayoutParams)
    {
      super();
    }
    
    public LayoutParams(ViewGroup.MarginLayoutParams paramMarginLayoutParams)
    {
      super();
    }
    
    public LayoutParams(RecyclerView.LayoutParams paramLayoutParams)
    {
      super();
    }
    
    public int getSpanIndex()
    {
      return this.mSpanIndex;
    }
    
    public int getSpanSize()
    {
      return this.mSpanSize;
    }
  }
  
  public static abstract class SpanSizeLookup
  {
    private boolean mCacheSpanIndices = false;
    final SparseIntArray mSpanIndexCache = new SparseIntArray();
    
    int findReferenceIndexFromCache(int paramInt)
    {
      int i = 0;
      int j = this.mSpanIndexCache.size() - 1;
      while (i <= j)
      {
        int k = i + j >>> 1;
        if (this.mSpanIndexCache.keyAt(k) < paramInt) {
          i = k + 1;
        } else {
          j = k - 1;
        }
      }
      paramInt = i - 1;
      if ((paramInt >= 0) && (paramInt < this.mSpanIndexCache.size())) {}
      for (paramInt = this.mSpanIndexCache.keyAt(paramInt);; paramInt = -1) {
        return paramInt;
      }
    }
    
    int getCachedSpanIndex(int paramInt1, int paramInt2)
    {
      int i;
      if (!this.mCacheSpanIndices) {
        i = getSpanIndex(paramInt1, paramInt2);
      }
      for (;;)
      {
        return i;
        int j = this.mSpanIndexCache.get(paramInt1, -1);
        i = j;
        if (j == -1)
        {
          i = getSpanIndex(paramInt1, paramInt2);
          this.mSpanIndexCache.put(paramInt1, i);
        }
      }
    }
    
    public int getSpanGroupIndex(int paramInt1, int paramInt2)
    {
      int i = 0;
      int j = 0;
      int k = getSpanSize(paramInt1);
      int m = 0;
      if (m < paramInt1)
      {
        int n = getSpanSize(m);
        int i1 = i + n;
        int i2;
        if (i1 == paramInt2)
        {
          i = 0;
          i2 = j + 1;
        }
        for (;;)
        {
          m++;
          j = i2;
          break;
          i2 = j;
          i = i1;
          if (i1 > paramInt2)
          {
            i = n;
            i2 = j + 1;
          }
        }
      }
      paramInt1 = j;
      if (i + k > paramInt2) {
        paramInt1 = j + 1;
      }
      return paramInt1;
    }
    
    public int getSpanIndex(int paramInt1, int paramInt2)
    {
      int i = getSpanSize(paramInt1);
      if (i == paramInt2) {
        paramInt1 = 0;
      }
      for (;;)
      {
        return paramInt1;
        int j = 0;
        int k = 0;
        int m = j;
        int n = k;
        if (this.mCacheSpanIndices)
        {
          m = j;
          n = k;
          if (this.mSpanIndexCache.size() > 0)
          {
            int i1 = findReferenceIndexFromCache(paramInt1);
            m = j;
            n = k;
            if (i1 >= 0)
            {
              m = this.mSpanIndexCache.get(i1) + getSpanSize(i1);
              n = i1 + 1;
            }
          }
        }
        j = n;
        n = m;
        m = j;
        if (m < paramInt1)
        {
          j = getSpanSize(m);
          k = n + j;
          if (k == paramInt2) {
            n = 0;
          }
          for (;;)
          {
            m++;
            break;
            n = k;
            if (k > paramInt2) {
              n = j;
            }
          }
        }
        paramInt1 = n;
        if (n + i > paramInt2) {
          paramInt1 = 0;
        }
      }
    }
    
    public abstract int getSpanSize(int paramInt);
    
    public void invalidateSpanIndexCache()
    {
      this.mSpanIndexCache.clear();
    }
    
    public boolean isSpanIndexCacheEnabled()
    {
      return this.mCacheSpanIndices;
    }
    
    public void setSpanIndexCacheEnabled(boolean paramBoolean)
    {
      this.mCacheSpanIndices = paramBoolean;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/support/widget/GridLayoutManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */