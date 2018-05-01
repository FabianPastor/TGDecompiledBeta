package org.telegram.messenger.support.widget;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.CollectionItemInfoCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.accessibility.AccessibilityEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

public class StaggeredGridLayoutManager
  extends RecyclerView.LayoutManager
  implements RecyclerView.SmoothScroller.ScrollVectorProvider
{
  static final boolean DEBUG = false;
  @Deprecated
  public static final int GAP_HANDLING_LAZY = 1;
  public static final int GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS = 2;
  public static final int GAP_HANDLING_NONE = 0;
  public static final int HORIZONTAL = 0;
  static final int INVALID_OFFSET = Integer.MIN_VALUE;
  private static final float MAX_SCROLL_FACTOR = 0.33333334F;
  private static final String TAG = "StaggeredGridLManager";
  public static final int VERTICAL = 1;
  private final AnchorInfo mAnchorInfo = new AnchorInfo();
  private final Runnable mCheckForGapsRunnable = new Runnable()
  {
    public void run()
    {
      StaggeredGridLayoutManager.this.checkForGaps();
    }
  };
  private int mFullSizeSpec;
  private int mGapStrategy = 2;
  private boolean mLaidOutInvalidFullSpan = false;
  private boolean mLastLayoutFromEnd;
  private boolean mLastLayoutRTL;
  private final LayoutState mLayoutState;
  LazySpanLookup mLazySpanLookup = new LazySpanLookup();
  private int mOrientation;
  private SavedState mPendingSavedState;
  int mPendingScrollPosition = -1;
  int mPendingScrollPositionOffset = Integer.MIN_VALUE;
  private int[] mPrefetchDistances;
  OrientationHelper mPrimaryOrientation;
  private BitSet mRemainingSpans;
  boolean mReverseLayout = false;
  OrientationHelper mSecondaryOrientation;
  boolean mShouldReverseLayout = false;
  private int mSizePerSpan;
  private boolean mSmoothScrollbarEnabled = true;
  private int mSpanCount = -1;
  Span[] mSpans;
  private final Rect mTmpRect = new Rect();
  
  public StaggeredGridLayoutManager(int paramInt1, int paramInt2)
  {
    this.mOrientation = paramInt2;
    setSpanCount(paramInt1);
    this.mLayoutState = new LayoutState();
    createOrientationHelpers();
  }
  
  private void appendViewToAllSpans(View paramView)
  {
    for (int i = this.mSpanCount - 1; i >= 0; i--) {
      this.mSpans[i].appendToSpan(paramView);
    }
  }
  
  private void applyPendingSavedState(AnchorInfo paramAnchorInfo)
  {
    if (this.mPendingSavedState.mSpanOffsetsSize > 0) {
      if (this.mPendingSavedState.mSpanOffsetsSize == this.mSpanCount)
      {
        int i = 0;
        if (i < this.mSpanCount)
        {
          this.mSpans[i].clear();
          int j = this.mPendingSavedState.mSpanOffsets[i];
          int k = j;
          if (j != Integer.MIN_VALUE) {
            if (!this.mPendingSavedState.mAnchorLayoutFromEnd) {
              break label100;
            }
          }
          label100:
          for (k = j + this.mPrimaryOrientation.getEndAfterPadding();; k = j + this.mPrimaryOrientation.getStartAfterPadding())
          {
            this.mSpans[i].setLine(k);
            i++;
            break;
          }
        }
      }
      else
      {
        this.mPendingSavedState.invalidateSpanInfo();
        this.mPendingSavedState.mAnchorPosition = this.mPendingSavedState.mVisibleAnchorPosition;
      }
    }
    this.mLastLayoutRTL = this.mPendingSavedState.mLastLayoutRTL;
    setReverseLayout(this.mPendingSavedState.mReverseLayout);
    resolveShouldLayoutReverse();
    if (this.mPendingSavedState.mAnchorPosition != -1) {
      this.mPendingScrollPosition = this.mPendingSavedState.mAnchorPosition;
    }
    for (paramAnchorInfo.mLayoutFromEnd = this.mPendingSavedState.mAnchorLayoutFromEnd;; paramAnchorInfo.mLayoutFromEnd = this.mShouldReverseLayout)
    {
      if (this.mPendingSavedState.mSpanLookupSize > 1)
      {
        this.mLazySpanLookup.mData = this.mPendingSavedState.mSpanLookup;
        this.mLazySpanLookup.mFullSpanItems = this.mPendingSavedState.mFullSpanItems;
      }
      return;
    }
  }
  
  private void attachViewToSpans(View paramView, LayoutParams paramLayoutParams, LayoutState paramLayoutState)
  {
    if (paramLayoutState.mLayoutDirection == 1) {
      if (paramLayoutParams.mFullSpan) {
        appendViewToAllSpans(paramView);
      }
    }
    for (;;)
    {
      return;
      paramLayoutParams.mSpan.appendToSpan(paramView);
      continue;
      if (paramLayoutParams.mFullSpan) {
        prependViewToAllSpans(paramView);
      } else {
        paramLayoutParams.mSpan.prependToSpan(paramView);
      }
    }
  }
  
  private int calculateScrollDirectionForPosition(int paramInt)
  {
    int i = -1;
    int j = 1;
    if (getChildCount() == 0)
    {
      if (this.mShouldReverseLayout) {}
      for (paramInt = j;; paramInt = -1) {
        return paramInt;
      }
    }
    int k;
    if (paramInt < getFirstChildPosition())
    {
      k = 1;
      label38:
      if (k == this.mShouldReverseLayout) {
        break label58;
      }
    }
    label58:
    for (paramInt = i;; paramInt = 1)
    {
      break;
      k = 0;
      break label38;
    }
  }
  
  private boolean checkSpanForGap(Span paramSpan)
  {
    boolean bool = true;
    if (this.mShouldReverseLayout)
    {
      if (paramSpan.getEndLine() >= this.mPrimaryOrientation.getEndAfterPadding()) {
        break label99;
      }
      if (paramSpan.getLayoutParams((View)paramSpan.mViews.get(paramSpan.mViews.size() - 1)).mFullSpan) {}
    }
    for (;;)
    {
      return bool;
      bool = false;
      continue;
      if (paramSpan.getStartLine() > this.mPrimaryOrientation.getStartAfterPadding())
      {
        if (paramSpan.getLayoutParams((View)paramSpan.mViews.get(0)).mFullSpan) {
          bool = false;
        }
      }
      else {
        label99:
        bool = false;
      }
    }
  }
  
  private int computeScrollExtent(RecyclerView.State paramState)
  {
    boolean bool1 = true;
    int i = 0;
    if (getChildCount() == 0) {
      return i;
    }
    OrientationHelper localOrientationHelper = this.mPrimaryOrientation;
    label29:
    View localView;
    if (!this.mSmoothScrollbarEnabled)
    {
      bool2 = true;
      localView = findFirstVisibleItemClosestToStart(bool2);
      if (this.mSmoothScrollbarEnabled) {
        break label76;
      }
    }
    label76:
    for (boolean bool2 = bool1;; bool2 = false)
    {
      i = ScrollbarHelper.computeScrollExtent(paramState, localOrientationHelper, localView, findFirstVisibleItemClosestToEnd(bool2), this, this.mSmoothScrollbarEnabled);
      break;
      bool2 = false;
      break label29;
    }
  }
  
  private int computeScrollOffset(RecyclerView.State paramState)
  {
    boolean bool1 = true;
    int i = 0;
    if (getChildCount() == 0) {
      return i;
    }
    OrientationHelper localOrientationHelper = this.mPrimaryOrientation;
    label29:
    View localView;
    if (!this.mSmoothScrollbarEnabled)
    {
      bool2 = true;
      localView = findFirstVisibleItemClosestToStart(bool2);
      if (this.mSmoothScrollbarEnabled) {
        break label80;
      }
    }
    label80:
    for (boolean bool2 = bool1;; bool2 = false)
    {
      i = ScrollbarHelper.computeScrollOffset(paramState, localOrientationHelper, localView, findFirstVisibleItemClosestToEnd(bool2), this, this.mSmoothScrollbarEnabled, this.mShouldReverseLayout);
      break;
      bool2 = false;
      break label29;
    }
  }
  
  private int computeScrollRange(RecyclerView.State paramState)
  {
    boolean bool1 = true;
    int i = 0;
    if (getChildCount() == 0) {
      return i;
    }
    OrientationHelper localOrientationHelper = this.mPrimaryOrientation;
    label29:
    View localView;
    if (!this.mSmoothScrollbarEnabled)
    {
      bool2 = true;
      localView = findFirstVisibleItemClosestToStart(bool2);
      if (this.mSmoothScrollbarEnabled) {
        break label76;
      }
    }
    label76:
    for (boolean bool2 = bool1;; bool2 = false)
    {
      i = ScrollbarHelper.computeScrollRange(paramState, localOrientationHelper, localView, findFirstVisibleItemClosestToEnd(bool2), this, this.mSmoothScrollbarEnabled);
      break;
      bool2 = false;
      break label29;
    }
  }
  
  private int convertFocusDirectionToLayoutDirection(int paramInt)
  {
    int i = -1;
    int j = Integer.MIN_VALUE;
    int k = 1;
    switch (paramInt)
    {
    default: 
      paramInt = Integer.MIN_VALUE;
    case 1: 
    case 2: 
    case 33: 
    case 130: 
    case 17: 
      for (;;)
      {
        return paramInt;
        paramInt = i;
        if (this.mOrientation != 1)
        {
          paramInt = i;
          if (isLayoutRTL())
          {
            paramInt = 1;
            continue;
            if (this.mOrientation == 1)
            {
              paramInt = 1;
            }
            else
            {
              paramInt = i;
              if (!isLayoutRTL())
              {
                paramInt = 1;
                continue;
                paramInt = i;
                if (this.mOrientation != 1)
                {
                  paramInt = Integer.MIN_VALUE;
                  continue;
                  paramInt = j;
                  if (this.mOrientation == 1) {
                    paramInt = 1;
                  }
                  continue;
                  paramInt = i;
                  if (this.mOrientation != 0) {
                    paramInt = Integer.MIN_VALUE;
                  }
                }
              }
            }
          }
        }
      }
    }
    if (this.mOrientation == 0) {}
    for (paramInt = k;; paramInt = Integer.MIN_VALUE) {
      break;
    }
  }
  
  private StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem createFullSpanItemFromEnd(int paramInt)
  {
    StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem localFullSpanItem = new StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem();
    localFullSpanItem.mGapPerSpan = new int[this.mSpanCount];
    for (int i = 0; i < this.mSpanCount; i++) {
      localFullSpanItem.mGapPerSpan[i] = (paramInt - this.mSpans[i].getEndLine(paramInt));
    }
    return localFullSpanItem;
  }
  
  private StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem createFullSpanItemFromStart(int paramInt)
  {
    StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem localFullSpanItem = new StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem();
    localFullSpanItem.mGapPerSpan = new int[this.mSpanCount];
    for (int i = 0; i < this.mSpanCount; i++) {
      localFullSpanItem.mGapPerSpan[i] = (this.mSpans[i].getStartLine(paramInt) - paramInt);
    }
    return localFullSpanItem;
  }
  
  private void createOrientationHelpers()
  {
    this.mPrimaryOrientation = OrientationHelper.createOrientationHelper(this, this.mOrientation);
    this.mSecondaryOrientation = OrientationHelper.createOrientationHelper(this, 1 - this.mOrientation);
  }
  
  private int fill(RecyclerView.Recycler paramRecycler, LayoutState paramLayoutState, RecyclerView.State paramState)
  {
    this.mRemainingSpans.set(0, this.mSpanCount, true);
    int j;
    label62:
    int k;
    label65:
    View localView;
    LayoutParams localLayoutParams;
    int m;
    int n;
    label137:
    Span localSpan;
    label158:
    label169:
    label190:
    label223:
    int i1;
    int i2;
    int i3;
    StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem localFullSpanItem;
    if (this.mLayoutState.mInfinite) {
      if (paramLayoutState.mLayoutDirection == 1)
      {
        i = Integer.MAX_VALUE;
        updateAllRemainingSpans(paramLayoutState.mLayoutDirection, i);
        if (!this.mShouldReverseLayout) {
          break label519;
        }
        j = this.mPrimaryOrientation.getEndAfterPadding();
        k = 0;
        if ((!paramLayoutState.hasMore(paramState)) || ((!this.mLayoutState.mInfinite) && (this.mRemainingSpans.isEmpty()))) {
          break label917;
        }
        localView = paramLayoutState.next(paramRecycler);
        localLayoutParams = (LayoutParams)localView.getLayoutParams();
        m = localLayoutParams.getViewLayoutPosition();
        k = this.mLazySpanLookup.getSpan(m);
        if (k != -1) {
          break label531;
        }
        n = 1;
        if (n == 0) {
          break label547;
        }
        if (!localLayoutParams.mFullSpan) {
          break label537;
        }
        localSpan = this.mSpans[0];
        this.mLazySpanLookup.setSpan(m, localSpan);
        localLayoutParams.mSpan = localSpan;
        if (paramLayoutState.mLayoutDirection != 1) {
          break label559;
        }
        addView(localView);
        measureChildWithDecorationsAndMargin(localView, localLayoutParams, false);
        if (paramLayoutState.mLayoutDirection != 1) {
          break label581;
        }
        if (!localLayoutParams.mFullSpan) {
          break label569;
        }
        k = getMaxEnd(j);
        i1 = k + this.mPrimaryOrientation.getDecoratedMeasurement(localView);
        i2 = k;
        i3 = i1;
        if (n != 0)
        {
          i2 = k;
          i3 = i1;
          if (localLayoutParams.mFullSpan)
          {
            localFullSpanItem = createFullSpanItemFromEnd(k);
            localFullSpanItem.mGapDir = -1;
            localFullSpanItem.mPosition = m;
            this.mLazySpanLookup.addFullSpanItem(localFullSpanItem);
            i3 = i1;
            i2 = k;
          }
        }
        if ((localLayoutParams.mFullSpan) && (paramLayoutState.mItemDirection == -1))
        {
          if (n == 0) {
            break label693;
          }
          this.mLaidOutInvalidFullSpan = true;
        }
        attachViewToSpans(localView, localLayoutParams, paramLayoutState);
        if ((!isLayoutRTL()) || (this.mOrientation != 1)) {
          break label801;
        }
        if (!localLayoutParams.mFullSpan) {
          break label771;
        }
        k = this.mSecondaryOrientation.getEndAfterPadding();
        label371:
        n = k - this.mSecondaryOrientation.getDecoratedMeasurement(localView);
        if (this.mOrientation != 1) {
          break label866;
        }
        layoutDecoratedWithMargins(localView, n, i2, k, i3);
        label407:
        if (!localLayoutParams.mFullSpan) {
          break label883;
        }
        updateAllRemainingSpans(this.mLayoutState.mLayoutDirection, i);
        label428:
        recycle(paramRecycler, this.mLayoutState);
        if ((this.mLayoutState.mStopInFocusable) && (localView.hasFocusable()))
        {
          if (!localLayoutParams.mFullSpan) {
            break label901;
          }
          this.mRemainingSpans.clear();
        }
      }
    }
    for (;;)
    {
      k = 1;
      break label65;
      i = Integer.MIN_VALUE;
      break;
      if (paramLayoutState.mLayoutDirection == 1)
      {
        i = paramLayoutState.mEndLine + paramLayoutState.mAvailable;
        break;
      }
      i = paramLayoutState.mStartLine - paramLayoutState.mAvailable;
      break;
      label519:
      j = this.mPrimaryOrientation.getStartAfterPadding();
      break label62;
      label531:
      n = 0;
      break label137;
      label537:
      localSpan = getNextSpan(paramLayoutState);
      break label158;
      label547:
      localSpan = this.mSpans[k];
      break label169;
      label559:
      addView(localView, 0);
      break label190;
      label569:
      k = localSpan.getEndLine(j);
      break label223;
      label581:
      if (localLayoutParams.mFullSpan) {}
      for (k = getMinStart(j);; k = localSpan.getStartLine(j))
      {
        i1 = k - this.mPrimaryOrientation.getDecoratedMeasurement(localView);
        i2 = i1;
        i3 = k;
        if (n == 0) {
          break;
        }
        i2 = i1;
        i3 = k;
        if (!localLayoutParams.mFullSpan) {
          break;
        }
        localFullSpanItem = createFullSpanItemFromStart(k);
        localFullSpanItem.mGapDir = 1;
        localFullSpanItem.mPosition = m;
        this.mLazySpanLookup.addFullSpanItem(localFullSpanItem);
        i2 = i1;
        i3 = k;
        break;
      }
      label693:
      if (paramLayoutState.mLayoutDirection == 1)
      {
        if (!areAllEndsEqual()) {}
        for (k = 1;; k = 0)
        {
          label711:
          if (k == 0) {
            break label763;
          }
          localFullSpanItem = this.mLazySpanLookup.getFullSpanItem(m);
          if (localFullSpanItem != null) {
            localFullSpanItem.mHasUnwantedGapAfter = true;
          }
          this.mLaidOutInvalidFullSpan = true;
          break;
        }
      }
      if (!areAllStartsEqual()) {}
      for (k = 1;; k = 0)
      {
        break label711;
        label763:
        break;
      }
      label771:
      k = this.mSecondaryOrientation.getEndAfterPadding() - (this.mSpanCount - 1 - localSpan.mIndex) * this.mSizePerSpan;
      break label371;
      label801:
      if (localLayoutParams.mFullSpan) {}
      for (k = this.mSecondaryOrientation.getStartAfterPadding();; k = localSpan.mIndex * this.mSizePerSpan + this.mSecondaryOrientation.getStartAfterPadding())
      {
        i1 = k + this.mSecondaryOrientation.getDecoratedMeasurement(localView);
        n = k;
        k = i1;
        break;
      }
      label866:
      layoutDecoratedWithMargins(localView, i2, n, i3, k);
      break label407;
      label883:
      updateRemainingSpans(localSpan, this.mLayoutState.mLayoutDirection, i);
      break label428;
      label901:
      this.mRemainingSpans.set(localSpan.mIndex, false);
    }
    label917:
    if (k == 0) {
      recycle(paramRecycler, this.mLayoutState);
    }
    if (this.mLayoutState.mLayoutDirection == -1)
    {
      i = getMinStart(this.mPrimaryOrientation.getStartAfterPadding());
      i = this.mPrimaryOrientation.getStartAfterPadding() - i;
      if (i <= 0) {
        break label1010;
      }
    }
    label1010:
    for (int i = Math.min(paramLayoutState.mAvailable, i);; i = 0)
    {
      return i;
      i = getMaxEnd(this.mPrimaryOrientation.getEndAfterPadding()) - this.mPrimaryOrientation.getEndAfterPadding();
      break;
    }
  }
  
  private int findFirstReferenceChildPosition(int paramInt)
  {
    int i = getChildCount();
    int j = 0;
    int k;
    if (j < i)
    {
      k = getPosition(getChildAt(j));
      if ((k < 0) || (k >= paramInt)) {}
    }
    for (paramInt = k;; paramInt = 0)
    {
      return paramInt;
      j++;
      break;
    }
  }
  
  private int findLastReferenceChildPosition(int paramInt)
  {
    int i = getChildCount() - 1;
    int j;
    if (i >= 0)
    {
      j = getPosition(getChildAt(i));
      if ((j < 0) || (j >= paramInt)) {}
    }
    for (paramInt = j;; paramInt = 0)
    {
      return paramInt;
      i--;
      break;
    }
  }
  
  private void fixEndGap(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, boolean paramBoolean)
  {
    int i = getMaxEnd(Integer.MIN_VALUE);
    if (i == Integer.MIN_VALUE) {}
    for (;;)
    {
      return;
      i = this.mPrimaryOrientation.getEndAfterPadding() - i;
      if (i > 0)
      {
        i -= -scrollBy(-i, paramRecycler, paramState);
        if ((paramBoolean) && (i > 0)) {
          this.mPrimaryOrientation.offsetChildren(i);
        }
      }
    }
  }
  
  private void fixStartGap(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, boolean paramBoolean)
  {
    int i = getMinStart(Integer.MAX_VALUE);
    if (i == Integer.MAX_VALUE) {}
    for (;;)
    {
      return;
      i -= this.mPrimaryOrientation.getStartAfterPadding();
      if (i > 0)
      {
        i -= scrollBy(i, paramRecycler, paramState);
        if ((paramBoolean) && (i > 0)) {
          this.mPrimaryOrientation.offsetChildren(-i);
        }
      }
    }
  }
  
  private int getMaxEnd(int paramInt)
  {
    int i = this.mSpans[0].getEndLine(paramInt);
    int j = 1;
    while (j < this.mSpanCount)
    {
      int k = this.mSpans[j].getEndLine(paramInt);
      int m = i;
      if (k > i) {
        m = k;
      }
      j++;
      i = m;
    }
    return i;
  }
  
  private int getMaxStart(int paramInt)
  {
    int i = this.mSpans[0].getStartLine(paramInt);
    int j = 1;
    while (j < this.mSpanCount)
    {
      int k = this.mSpans[j].getStartLine(paramInt);
      int m = i;
      if (k > i) {
        m = k;
      }
      j++;
      i = m;
    }
    return i;
  }
  
  private int getMinEnd(int paramInt)
  {
    int i = this.mSpans[0].getEndLine(paramInt);
    int j = 1;
    while (j < this.mSpanCount)
    {
      int k = this.mSpans[j].getEndLine(paramInt);
      int m = i;
      if (k < i) {
        m = k;
      }
      j++;
      i = m;
    }
    return i;
  }
  
  private int getMinStart(int paramInt)
  {
    int i = this.mSpans[0].getStartLine(paramInt);
    int j = 1;
    while (j < this.mSpanCount)
    {
      int k = this.mSpans[j].getStartLine(paramInt);
      int m = i;
      if (k < i) {
        m = k;
      }
      j++;
      i = m;
    }
    return i;
  }
  
  private Span getNextSpan(LayoutState paramLayoutState)
  {
    int i;
    int j;
    if (preferLastSpan(paramLayoutState.mLayoutDirection))
    {
      i = this.mSpanCount - 1;
      j = -1;
    }
    int m;
    int i1;
    for (int k = -1; paramLayoutState.mLayoutDirection == 1; k = 1)
    {
      paramLayoutState = null;
      m = Integer.MAX_VALUE;
      n = this.mPrimaryOrientation.getStartAfterPadding();
      for (;;)
      {
        localObject = paramLayoutState;
        if (i == j) {
          break;
        }
        localObject = this.mSpans[i];
        i1 = ((Span)localObject).getEndLine(n);
        i2 = m;
        if (i1 < m)
        {
          paramLayoutState = (LayoutState)localObject;
          i2 = i1;
        }
        i += k;
        m = i2;
      }
      i = 0;
      j = this.mSpanCount;
    }
    paramLayoutState = null;
    int i2 = Integer.MIN_VALUE;
    int n = this.mPrimaryOrientation.getEndAfterPadding();
    while (i != j)
    {
      localObject = this.mSpans[i];
      i1 = ((Span)localObject).getStartLine(n);
      m = i2;
      if (i1 > i2)
      {
        paramLayoutState = (LayoutState)localObject;
        m = i1;
      }
      i += k;
      i2 = m;
    }
    Object localObject = paramLayoutState;
    return (Span)localObject;
  }
  
  private void handleUpdate(int paramInt1, int paramInt2, int paramInt3)
  {
    int i;
    int j;
    int k;
    if (this.mShouldReverseLayout)
    {
      i = getLastChildPosition();
      if (paramInt3 != 8) {
        break label104;
      }
      if (paramInt1 >= paramInt2) {
        break label93;
      }
      j = paramInt2 + 1;
      k = paramInt1;
      label32:
      this.mLazySpanLookup.invalidateAfter(k);
      switch (paramInt3)
      {
      default: 
        label76:
        if (j > i) {
          break;
        }
      }
    }
    label93:
    label104:
    label191:
    for (;;)
    {
      return;
      i = getFirstChildPosition();
      break;
      j = paramInt1 + 1;
      k = paramInt2;
      break label32;
      k = paramInt1;
      j = paramInt1 + paramInt2;
      break label32;
      this.mLazySpanLookup.offsetForAddition(paramInt1, paramInt2);
      break label76;
      this.mLazySpanLookup.offsetForRemoval(paramInt1, paramInt2);
      break label76;
      this.mLazySpanLookup.offsetForRemoval(paramInt1, 1);
      this.mLazySpanLookup.offsetForAddition(paramInt2, 1);
      break label76;
      if (this.mShouldReverseLayout) {}
      for (paramInt1 = getFirstChildPosition();; paramInt1 = getLastChildPosition())
      {
        if (k > paramInt1) {
          break label191;
        }
        requestLayout();
        break;
      }
    }
  }
  
  private void measureChildWithDecorationsAndMargin(View paramView, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    calculateItemDecorationsForChild(paramView, this.mTmpRect);
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    paramInt1 = updateSpecWithExtra(paramInt1, localLayoutParams.leftMargin + this.mTmpRect.left, localLayoutParams.rightMargin + this.mTmpRect.right);
    paramInt2 = updateSpecWithExtra(paramInt2, localLayoutParams.topMargin + this.mTmpRect.top, localLayoutParams.bottomMargin + this.mTmpRect.bottom);
    if (paramBoolean) {}
    for (paramBoolean = shouldReMeasureChild(paramView, paramInt1, paramInt2, localLayoutParams);; paramBoolean = shouldMeasureChild(paramView, paramInt1, paramInt2, localLayoutParams))
    {
      if (paramBoolean) {
        paramView.measure(paramInt1, paramInt2);
      }
      return;
    }
  }
  
  private void measureChildWithDecorationsAndMargin(View paramView, LayoutParams paramLayoutParams, boolean paramBoolean)
  {
    if (paramLayoutParams.mFullSpan) {
      if (this.mOrientation == 1) {
        measureChildWithDecorationsAndMargin(paramView, this.mFullSizeSpec, getChildMeasureSpec(getHeight(), getHeightMode(), getPaddingTop() + getPaddingBottom(), paramLayoutParams.height, true), paramBoolean);
      }
    }
    for (;;)
    {
      return;
      measureChildWithDecorationsAndMargin(paramView, getChildMeasureSpec(getWidth(), getWidthMode(), getPaddingLeft() + getPaddingRight(), paramLayoutParams.width, true), this.mFullSizeSpec, paramBoolean);
      continue;
      if (this.mOrientation == 1) {
        measureChildWithDecorationsAndMargin(paramView, getChildMeasureSpec(this.mSizePerSpan, getWidthMode(), 0, paramLayoutParams.width, false), getChildMeasureSpec(getHeight(), getHeightMode(), getPaddingTop() + getPaddingBottom(), paramLayoutParams.height, true), paramBoolean);
      } else {
        measureChildWithDecorationsAndMargin(paramView, getChildMeasureSpec(getWidth(), getWidthMode(), getPaddingLeft() + getPaddingRight(), paramLayoutParams.width, true), getChildMeasureSpec(this.mSizePerSpan, getHeightMode(), 0, paramLayoutParams.height, false), paramBoolean);
      }
    }
  }
  
  private void onLayoutChildren(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, boolean paramBoolean)
  {
    int i = 1;
    AnchorInfo localAnchorInfo = this.mAnchorInfo;
    if (((this.mPendingSavedState != null) || (this.mPendingScrollPosition != -1)) && (paramState.getItemCount() == 0))
    {
      removeAndRecycleAllViews(paramRecycler);
      localAnchorInfo.reset();
      return;
    }
    if ((!localAnchorInfo.mValid) || (this.mPendingScrollPosition != -1) || (this.mPendingSavedState != null))
    {
      j = 1;
      if (j != 0)
      {
        localAnchorInfo.reset();
        if (this.mPendingSavedState == null) {
          break label247;
        }
        applyPendingSavedState(localAnchorInfo);
      }
    }
    for (;;)
    {
      updateAnchorInfoForLayout(paramState, localAnchorInfo);
      localAnchorInfo.mValid = true;
      if ((this.mPendingSavedState == null) && (this.mPendingScrollPosition == -1) && ((localAnchorInfo.mLayoutFromEnd != this.mLastLayoutFromEnd) || (isLayoutRTL() != this.mLastLayoutRTL)))
      {
        this.mLazySpanLookup.clear();
        localAnchorInfo.mInvalidateOffsets = true;
      }
      if ((getChildCount() <= 0) || ((this.mPendingSavedState != null) && (this.mPendingSavedState.mSpanOffsetsSize >= 1))) {
        break label326;
      }
      if (!localAnchorInfo.mInvalidateOffsets) {
        break label263;
      }
      for (j = 0; j < this.mSpanCount; j++)
      {
        this.mSpans[j].clear();
        if (localAnchorInfo.mOffset != Integer.MIN_VALUE) {
          this.mSpans[j].setLine(localAnchorInfo.mOffset);
        }
      }
      j = 0;
      break;
      label247:
      resolveShouldLayoutReverse();
      localAnchorInfo.mLayoutFromEnd = this.mShouldReverseLayout;
    }
    label263:
    label326:
    label425:
    label457:
    int k;
    int m;
    if ((j != 0) || (this.mAnchorInfo.mSpanReferenceLines == null))
    {
      for (j = 0; j < this.mSpanCount; j++) {
        this.mSpans[j].cacheReferenceLineAndClear(this.mShouldReverseLayout, localAnchorInfo.mOffset);
      }
      this.mAnchorInfo.saveSpanReferenceLines(this.mSpans);
      detachAndScrapAttachedViews(paramRecycler);
      this.mLayoutState.mRecycle = false;
      this.mLaidOutInvalidFullSpan = false;
      updateMeasureSpecs(this.mSecondaryOrientation.getTotalSpace());
      updateLayoutState(localAnchorInfo.mPosition, paramState);
      if (!localAnchorInfo.mLayoutFromEnd) {
        break label647;
      }
      setLayoutStateDirection(-1);
      fill(paramRecycler, this.mLayoutState, paramState);
      setLayoutStateDirection(1);
      this.mLayoutState.mCurrentPosition = (localAnchorInfo.mPosition + this.mLayoutState.mItemDirection);
      fill(paramRecycler, this.mLayoutState, paramState);
      repositionToWrapContentIfNecessary();
      if (getChildCount() > 0)
      {
        if (!this.mShouldReverseLayout) {
          break label702;
        }
        fixEndGap(paramRecycler, paramState, true);
        fixStartGap(paramRecycler, paramState, false);
      }
      k = 0;
      m = k;
      if (paramBoolean)
      {
        m = k;
        if (!paramState.isPreLayout())
        {
          if ((this.mGapStrategy == 0) || (getChildCount() <= 0)) {
            break label719;
          }
          j = i;
          if (!this.mLaidOutInvalidFullSpan) {
            if (hasGapsToFix() == null) {
              break label719;
            }
          }
        }
      }
    }
    label647:
    label702:
    label719:
    for (int j = i;; j = 0)
    {
      m = k;
      if (j != 0)
      {
        removeCallbacks(this.mCheckForGapsRunnable);
        m = k;
        if (checkForGaps()) {
          m = 1;
        }
      }
      if (paramState.isPreLayout()) {
        this.mAnchorInfo.reset();
      }
      this.mLastLayoutFromEnd = localAnchorInfo.mLayoutFromEnd;
      this.mLastLayoutRTL = isLayoutRTL();
      if (m == 0) {
        break;
      }
      this.mAnchorInfo.reset();
      onLayoutChildren(paramRecycler, paramState, false);
      break;
      for (j = 0; j < this.mSpanCount; j++)
      {
        Span localSpan = this.mSpans[j];
        localSpan.clear();
        localSpan.setLine(this.mAnchorInfo.mSpanReferenceLines[j]);
      }
      break label326;
      setLayoutStateDirection(1);
      fill(paramRecycler, this.mLayoutState, paramState);
      setLayoutStateDirection(-1);
      this.mLayoutState.mCurrentPosition = (localAnchorInfo.mPosition + this.mLayoutState.mItemDirection);
      fill(paramRecycler, this.mLayoutState, paramState);
      break label425;
      fixStartGap(paramRecycler, paramState, true);
      fixEndGap(paramRecycler, paramState, false);
      break label457;
    }
  }
  
  private boolean preferLastSpan(int paramInt)
  {
    boolean bool1 = true;
    boolean bool2;
    if (this.mOrientation == 0)
    {
      if (paramInt == -1)
      {
        bool2 = true;
        if (bool2 == this.mShouldReverseLayout) {
          break label33;
        }
      }
      label33:
      for (bool2 = bool1;; bool2 = false)
      {
        return bool2;
        bool2 = false;
        break;
      }
    }
    if (paramInt == -1)
    {
      bool2 = true;
      label45:
      if (bool2 != this.mShouldReverseLayout) {
        break label77;
      }
    }
    label77:
    for (int i = 1;; i = 0)
    {
      bool2 = bool1;
      if (i == isLayoutRTL()) {
        break;
      }
      bool2 = false;
      break;
      bool2 = false;
      break label45;
    }
  }
  
  private void prependViewToAllSpans(View paramView)
  {
    for (int i = this.mSpanCount - 1; i >= 0; i--) {
      this.mSpans[i].prependToSpan(paramView);
    }
  }
  
  private void recycle(RecyclerView.Recycler paramRecycler, LayoutState paramLayoutState)
  {
    if ((!paramLayoutState.mRecycle) || (paramLayoutState.mInfinite)) {}
    for (;;)
    {
      return;
      if (paramLayoutState.mAvailable != 0) {
        break;
      }
      if (paramLayoutState.mLayoutDirection == -1) {
        recycleFromEnd(paramRecycler, paramLayoutState.mEndLine);
      } else {
        recycleFromStart(paramRecycler, paramLayoutState.mStartLine);
      }
    }
    if (paramLayoutState.mLayoutDirection == -1)
    {
      i = paramLayoutState.mStartLine - getMaxStart(paramLayoutState.mStartLine);
      if (i < 0) {}
      for (i = paramLayoutState.mEndLine;; i = paramLayoutState.mEndLine - Math.min(i, paramLayoutState.mAvailable))
      {
        recycleFromEnd(paramRecycler, i);
        break;
      }
    }
    int i = getMinEnd(paramLayoutState.mEndLine) - paramLayoutState.mEndLine;
    if (i < 0) {}
    for (i = paramLayoutState.mStartLine;; i = paramLayoutState.mStartLine + Math.min(i, paramLayoutState.mAvailable))
    {
      recycleFromStart(paramRecycler, i);
      break;
    }
  }
  
  private void recycleFromEnd(RecyclerView.Recycler paramRecycler, int paramInt)
  {
    for (int i = getChildCount() - 1;; i--)
    {
      View localView;
      LayoutParams localLayoutParams;
      if (i >= 0)
      {
        localView = getChildAt(i);
        if ((this.mPrimaryOrientation.getDecoratedStart(localView) >= paramInt) && (this.mPrimaryOrientation.getTransformedStartWithDecoration(localView) >= paramInt))
        {
          localLayoutParams = (LayoutParams)localView.getLayoutParams();
          if (!localLayoutParams.mFullSpan) {
            break label126;
          }
          j = 0;
          if (j >= this.mSpanCount) {
            break label98;
          }
          if (this.mSpans[j].mViews.size() != 1) {
            break label92;
          }
        }
      }
      label92:
      label98:
      label126:
      while (localLayoutParams.mSpan.mViews.size() == 1)
      {
        for (;;)
        {
          return;
          j++;
        }
        for (int j = 0; j < this.mSpanCount; j++) {
          this.mSpans[j].popEnd();
        }
      }
      localLayoutParams.mSpan.popEnd();
      removeAndRecycleView(localView, paramRecycler);
    }
  }
  
  private void recycleFromStart(RecyclerView.Recycler paramRecycler, int paramInt)
  {
    for (;;)
    {
      View localView;
      LayoutParams localLayoutParams;
      if (getChildCount() > 0)
      {
        localView = getChildAt(0);
        if ((this.mPrimaryOrientation.getDecoratedEnd(localView) <= paramInt) && (this.mPrimaryOrientation.getTransformedEndWithDecoration(localView) <= paramInt))
        {
          localLayoutParams = (LayoutParams)localView.getLayoutParams();
          if (!localLayoutParams.mFullSpan) {
            break label118;
          }
          i = 0;
          if (i >= this.mSpanCount) {
            break label90;
          }
          if (this.mSpans[i].mViews.size() != 1) {
            break label84;
          }
        }
      }
      label84:
      label90:
      label118:
      while (localLayoutParams.mSpan.mViews.size() == 1)
      {
        for (;;)
        {
          return;
          i++;
        }
        for (int i = 0; i < this.mSpanCount; i++) {
          this.mSpans[i].popStart();
        }
      }
      localLayoutParams.mSpan.popStart();
      removeAndRecycleView(localView, paramRecycler);
    }
  }
  
  private void repositionToWrapContentIfNecessary()
  {
    if (this.mSecondaryOrientation.getMode() == NUM) {}
    int i;
    Object localObject;
    int k;
    int m;
    do
    {
      return;
      float f1 = 0.0F;
      i = getChildCount();
      j = 0;
      if (j < i)
      {
        localObject = getChildAt(j);
        float f2 = this.mSecondaryOrientation.getDecoratedMeasurement((View)localObject);
        if (f2 < f1) {}
        for (;;)
        {
          j++;
          break;
          float f3 = f2;
          if (((LayoutParams)((View)localObject).getLayoutParams()).isFullSpan()) {
            f3 = 1.0F * f2 / this.mSpanCount;
          }
          f1 = Math.max(f1, f3);
        }
      }
      k = this.mSizePerSpan;
      m = Math.round(this.mSpanCount * f1);
      j = m;
      if (this.mSecondaryOrientation.getMode() == Integer.MIN_VALUE) {
        j = Math.min(m, this.mSecondaryOrientation.getTotalSpace());
      }
      updateMeasureSpecs(j);
    } while (this.mSizePerSpan == k);
    int j = 0;
    label162:
    View localView;
    if (j < i)
    {
      localView = getChildAt(j);
      localObject = (LayoutParams)localView.getLayoutParams();
      if (!((LayoutParams)localObject).mFullSpan) {
        break label198;
      }
    }
    for (;;)
    {
      j++;
      break label162;
      break;
      label198:
      if ((isLayoutRTL()) && (this.mOrientation == 1))
      {
        localView.offsetLeftAndRight(-(this.mSpanCount - 1 - ((LayoutParams)localObject).mSpan.mIndex) * this.mSizePerSpan - -(this.mSpanCount - 1 - ((LayoutParams)localObject).mSpan.mIndex) * k);
      }
      else
      {
        int n = ((LayoutParams)localObject).mSpan.mIndex * this.mSizePerSpan;
        m = ((LayoutParams)localObject).mSpan.mIndex * k;
        if (this.mOrientation == 1) {
          localView.offsetLeftAndRight(n - m);
        } else {
          localView.offsetTopAndBottom(n - m);
        }
      }
    }
  }
  
  private void resolveShouldLayoutReverse()
  {
    boolean bool = true;
    if ((this.mOrientation == 1) || (!isLayoutRTL()))
    {
      this.mShouldReverseLayout = this.mReverseLayout;
      return;
    }
    if (!this.mReverseLayout) {}
    for (;;)
    {
      this.mShouldReverseLayout = bool;
      break;
      bool = false;
    }
  }
  
  private void setLayoutStateDirection(int paramInt)
  {
    int i = 1;
    this.mLayoutState.mLayoutDirection = paramInt;
    LayoutState localLayoutState = this.mLayoutState;
    boolean bool1 = this.mShouldReverseLayout;
    boolean bool2;
    if (paramInt == -1)
    {
      bool2 = true;
      if (bool1 != bool2) {
        break label50;
      }
    }
    label50:
    for (paramInt = i;; paramInt = -1)
    {
      localLayoutState.mItemDirection = paramInt;
      return;
      bool2 = false;
      break;
    }
  }
  
  private void updateAllRemainingSpans(int paramInt1, int paramInt2)
  {
    int i = 0;
    if (i < this.mSpanCount)
    {
      if (this.mSpans[i].mViews.isEmpty()) {}
      for (;;)
      {
        i++;
        break;
        updateRemainingSpans(this.mSpans[i], paramInt1, paramInt2);
      }
    }
  }
  
  private boolean updateAnchorFromChildren(RecyclerView.State paramState, AnchorInfo paramAnchorInfo)
  {
    if (this.mLastLayoutFromEnd) {}
    for (int i = findLastReferenceChildPosition(paramState.getItemCount());; i = findFirstReferenceChildPosition(paramState.getItemCount()))
    {
      paramAnchorInfo.mPosition = i;
      paramAnchorInfo.mOffset = Integer.MIN_VALUE;
      return true;
    }
  }
  
  private void updateLayoutState(int paramInt, RecyclerView.State paramState)
  {
    boolean bool1 = true;
    this.mLayoutState.mAvailable = 0;
    this.mLayoutState.mCurrentPosition = paramInt;
    int i = 0;
    int j = 0;
    int k = j;
    int m = i;
    if (isSmoothScrolling())
    {
      int n = paramState.getTargetScrollPosition();
      k = j;
      m = i;
      if (n != -1)
      {
        boolean bool2 = this.mShouldReverseLayout;
        if (n >= paramInt) {
          break label186;
        }
        bool3 = true;
        if (bool2 != bool3) {
          break label192;
        }
        k = this.mPrimaryOrientation.getTotalSpace();
        m = i;
      }
    }
    label94:
    if (getClipToPadding())
    {
      this.mLayoutState.mStartLine = (this.mPrimaryOrientation.getStartAfterPadding() - m);
      this.mLayoutState.mEndLine = (this.mPrimaryOrientation.getEndAfterPadding() + k);
      label135:
      this.mLayoutState.mStopInFocusable = false;
      this.mLayoutState.mRecycle = true;
      paramState = this.mLayoutState;
      if ((this.mPrimaryOrientation.getMode() != 0) || (this.mPrimaryOrientation.getEnd() != 0)) {
        break label238;
      }
    }
    label186:
    label192:
    label238:
    for (boolean bool3 = bool1;; bool3 = false)
    {
      paramState.mInfinite = bool3;
      return;
      bool3 = false;
      break;
      m = this.mPrimaryOrientation.getTotalSpace();
      k = j;
      break label94;
      this.mLayoutState.mEndLine = (this.mPrimaryOrientation.getEnd() + k);
      this.mLayoutState.mStartLine = (-m);
      break label135;
    }
  }
  
  private void updateRemainingSpans(Span paramSpan, int paramInt1, int paramInt2)
  {
    int i = paramSpan.getDeletedSize();
    if (paramInt1 == -1) {
      if (paramSpan.getStartLine() + i <= paramInt2) {
        this.mRemainingSpans.set(paramSpan.mIndex, false);
      }
    }
    for (;;)
    {
      return;
      if (paramSpan.getEndLine() - i >= paramInt2) {
        this.mRemainingSpans.set(paramSpan.mIndex, false);
      }
    }
  }
  
  private int updateSpecWithExtra(int paramInt1, int paramInt2, int paramInt3)
  {
    int i;
    if ((paramInt2 == 0) && (paramInt3 == 0)) {
      i = paramInt1;
    }
    for (;;)
    {
      return i;
      int j = View.MeasureSpec.getMode(paramInt1);
      if (j != Integer.MIN_VALUE)
      {
        i = paramInt1;
        if (j != NUM) {}
      }
      else
      {
        i = View.MeasureSpec.makeMeasureSpec(Math.max(0, View.MeasureSpec.getSize(paramInt1) - paramInt2 - paramInt3), j);
      }
    }
  }
  
  boolean areAllEndsEqual()
  {
    boolean bool = false;
    int i = this.mSpans[0].getEndLine(Integer.MIN_VALUE);
    int j = 1;
    if (j < this.mSpanCount) {
      if (this.mSpans[j].getEndLine(Integer.MIN_VALUE) == i) {}
    }
    for (;;)
    {
      return bool;
      j++;
      break;
      bool = true;
    }
  }
  
  boolean areAllStartsEqual()
  {
    boolean bool = false;
    int i = this.mSpans[0].getStartLine(Integer.MIN_VALUE);
    int j = 1;
    if (j < this.mSpanCount) {
      if (this.mSpans[j].getStartLine(Integer.MIN_VALUE) == i) {}
    }
    for (;;)
    {
      return bool;
      j++;
      break;
      bool = true;
    }
  }
  
  public void assertNotInLayoutOrScroll(String paramString)
  {
    if (this.mPendingSavedState == null) {
      super.assertNotInLayoutOrScroll(paramString);
    }
  }
  
  public boolean canScrollHorizontally()
  {
    if (this.mOrientation == 0) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean canScrollVertically()
  {
    boolean bool = true;
    if (this.mOrientation == 1) {}
    for (;;)
    {
      return bool;
      bool = false;
    }
  }
  
  boolean checkForGaps()
  {
    boolean bool = true;
    if ((getChildCount() == 0) || (this.mGapStrategy == 0) || (!isAttachedToWindow())) {
      bool = false;
    }
    int i;
    int j;
    for (;;)
    {
      return bool;
      if (this.mShouldReverseLayout) {
        i = getLastChildPosition();
      }
      for (j = getFirstChildPosition();; j = getLastChildPosition())
      {
        if ((i != 0) || (hasGapsToFix() == null)) {
          break label86;
        }
        this.mLazySpanLookup.clear();
        requestSimpleAnimationsInNextLayout();
        requestLayout();
        break;
        i = getFirstChildPosition();
      }
      label86:
      if (this.mLaidOutInvalidFullSpan) {
        break;
      }
      bool = false;
    }
    if (this.mShouldReverseLayout) {}
    StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem localFullSpanItem1;
    for (int k = -1;; k = 1)
    {
      localFullSpanItem1 = this.mLazySpanLookup.getFirstFullSpanItemInRange(i, j + 1, k, true);
      if (localFullSpanItem1 != null) {
        break label156;
      }
      this.mLaidOutInvalidFullSpan = false;
      this.mLazySpanLookup.forceInvalidateAfter(j + 1);
      bool = false;
      break;
    }
    label156:
    StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem localFullSpanItem2 = this.mLazySpanLookup.getFirstFullSpanItemInRange(i, localFullSpanItem1.mPosition, k * -1, true);
    if (localFullSpanItem2 == null) {
      this.mLazySpanLookup.forceInvalidateAfter(localFullSpanItem1.mPosition);
    }
    for (;;)
    {
      requestSimpleAnimationsInNextLayout();
      requestLayout();
      break;
      this.mLazySpanLookup.forceInvalidateAfter(localFullSpanItem2.mPosition + 1);
    }
  }
  
  public boolean checkLayoutParams(RecyclerView.LayoutParams paramLayoutParams)
  {
    return paramLayoutParams instanceof LayoutParams;
  }
  
  public void collectAdjacentPrefetchPositions(int paramInt1, int paramInt2, RecyclerView.State paramState, RecyclerView.LayoutManager.LayoutPrefetchRegistry paramLayoutPrefetchRegistry)
  {
    if (this.mOrientation == 0) {
      if ((getChildCount() != 0) && (paramInt1 != 0)) {
        break label24;
      }
    }
    for (;;)
    {
      return;
      paramInt1 = paramInt2;
      break;
      label24:
      prepareLayoutStateForDelta(paramInt1, paramState);
      if ((this.mPrefetchDistances == null) || (this.mPrefetchDistances.length < this.mSpanCount)) {
        this.mPrefetchDistances = new int[this.mSpanCount];
      }
      paramInt1 = 0;
      paramInt2 = 0;
      if (paramInt2 < this.mSpanCount)
      {
        if (this.mLayoutState.mItemDirection == -1) {}
        for (int i = this.mLayoutState.mStartLine - this.mSpans[paramInt2].getStartLine(this.mLayoutState.mStartLine);; i = this.mSpans[paramInt2].getEndLine(this.mLayoutState.mEndLine) - this.mLayoutState.mEndLine)
        {
          int j = paramInt1;
          if (i >= 0)
          {
            this.mPrefetchDistances[paramInt1] = i;
            j = paramInt1 + 1;
          }
          paramInt2++;
          paramInt1 = j;
          break;
        }
      }
      Arrays.sort(this.mPrefetchDistances, 0, paramInt1);
      for (paramInt2 = 0; (paramInt2 < paramInt1) && (this.mLayoutState.hasMore(paramState)); paramInt2++)
      {
        paramLayoutPrefetchRegistry.addPosition(this.mLayoutState.mCurrentPosition, this.mPrefetchDistances[paramInt2]);
        LayoutState localLayoutState = this.mLayoutState;
        localLayoutState.mCurrentPosition += this.mLayoutState.mItemDirection;
      }
    }
  }
  
  public int computeHorizontalScrollExtent(RecyclerView.State paramState)
  {
    return computeScrollExtent(paramState);
  }
  
  public int computeHorizontalScrollOffset(RecyclerView.State paramState)
  {
    return computeScrollOffset(paramState);
  }
  
  public int computeHorizontalScrollRange(RecyclerView.State paramState)
  {
    return computeScrollRange(paramState);
  }
  
  public PointF computeScrollVectorForPosition(int paramInt)
  {
    paramInt = calculateScrollDirectionForPosition(paramInt);
    PointF localPointF = new PointF();
    if (paramInt == 0) {
      localPointF = null;
    }
    for (;;)
    {
      return localPointF;
      if (this.mOrientation == 0)
      {
        localPointF.x = paramInt;
        localPointF.y = 0.0F;
      }
      else
      {
        localPointF.x = 0.0F;
        localPointF.y = paramInt;
      }
    }
  }
  
  public int computeVerticalScrollExtent(RecyclerView.State paramState)
  {
    return computeScrollExtent(paramState);
  }
  
  public int computeVerticalScrollOffset(RecyclerView.State paramState)
  {
    return computeScrollOffset(paramState);
  }
  
  public int computeVerticalScrollRange(RecyclerView.State paramState)
  {
    return computeScrollRange(paramState);
  }
  
  public int[] findFirstCompletelyVisibleItemPositions(int[] paramArrayOfInt)
  {
    int[] arrayOfInt;
    if (paramArrayOfInt == null) {
      arrayOfInt = new int[this.mSpanCount];
    }
    do
    {
      for (int i = 0; i < this.mSpanCount; i++) {
        arrayOfInt[i] = this.mSpans[i].findFirstCompletelyVisibleItemPosition();
      }
      arrayOfInt = paramArrayOfInt;
    } while (paramArrayOfInt.length >= this.mSpanCount);
    throw new IllegalArgumentException("Provided int[]'s size must be more than or equal to span count. Expected:" + this.mSpanCount + ", array size:" + paramArrayOfInt.length);
    return arrayOfInt;
  }
  
  View findFirstVisibleItemClosestToEnd(boolean paramBoolean)
  {
    int i = this.mPrimaryOrientation.getStartAfterPadding();
    int j = this.mPrimaryOrientation.getEndAfterPadding();
    Object localObject1 = null;
    int k = getChildCount() - 1;
    View localView;
    for (;;)
    {
      if (k >= 0)
      {
        localView = getChildAt(k);
        int m = this.mPrimaryOrientation.getDecoratedStart(localView);
        int n = this.mPrimaryOrientation.getDecoratedEnd(localView);
        localObject2 = localObject1;
        if (n > i)
        {
          if (m >= j) {
            localObject2 = localObject1;
          }
        }
        else
        {
          k--;
          localObject1 = localObject2;
          continue;
        }
        localObject2 = localView;
        if (n > j) {
          if (paramBoolean) {
            break label113;
          }
        }
      }
    }
    for (Object localObject2 = localView;; localObject2 = localObject1)
    {
      return (View)localObject2;
      label113:
      localObject2 = localObject1;
      if (localObject1 != null) {
        break;
      }
      localObject2 = localView;
      break;
    }
  }
  
  View findFirstVisibleItemClosestToStart(boolean paramBoolean)
  {
    int i = this.mPrimaryOrientation.getStartAfterPadding();
    int j = this.mPrimaryOrientation.getEndAfterPadding();
    int k = getChildCount();
    Object localObject1 = null;
    int m = 0;
    View localView;
    for (;;)
    {
      if (m < k)
      {
        localView = getChildAt(m);
        int n = this.mPrimaryOrientation.getDecoratedStart(localView);
        localObject2 = localObject1;
        if (this.mPrimaryOrientation.getDecoratedEnd(localView) > i)
        {
          if (n >= j) {
            localObject2 = localObject1;
          }
        }
        else
        {
          m++;
          localObject1 = localObject2;
          continue;
        }
        localObject2 = localView;
        if (n < i) {
          if (paramBoolean) {
            break label112;
          }
        }
      }
    }
    for (Object localObject2 = localView;; localObject2 = localObject1)
    {
      return (View)localObject2;
      label112:
      localObject2 = localObject1;
      if (localObject1 != null) {
        break;
      }
      localObject2 = localView;
      break;
    }
  }
  
  int findFirstVisibleItemPositionInt()
  {
    View localView;
    if (this.mShouldReverseLayout)
    {
      localView = findFirstVisibleItemClosestToEnd(true);
      if (localView != null) {
        break label30;
      }
    }
    label30:
    for (int i = -1;; i = getPosition(localView))
    {
      return i;
      localView = findFirstVisibleItemClosestToStart(true);
      break;
    }
  }
  
  public int[] findFirstVisibleItemPositions(int[] paramArrayOfInt)
  {
    int[] arrayOfInt;
    if (paramArrayOfInt == null) {
      arrayOfInt = new int[this.mSpanCount];
    }
    do
    {
      for (int i = 0; i < this.mSpanCount; i++) {
        arrayOfInt[i] = this.mSpans[i].findFirstVisibleItemPosition();
      }
      arrayOfInt = paramArrayOfInt;
    } while (paramArrayOfInt.length >= this.mSpanCount);
    throw new IllegalArgumentException("Provided int[]'s size must be more than or equal to span count. Expected:" + this.mSpanCount + ", array size:" + paramArrayOfInt.length);
    return arrayOfInt;
  }
  
  public int[] findLastCompletelyVisibleItemPositions(int[] paramArrayOfInt)
  {
    int[] arrayOfInt;
    if (paramArrayOfInt == null) {
      arrayOfInt = new int[this.mSpanCount];
    }
    do
    {
      for (int i = 0; i < this.mSpanCount; i++) {
        arrayOfInt[i] = this.mSpans[i].findLastCompletelyVisibleItemPosition();
      }
      arrayOfInt = paramArrayOfInt;
    } while (paramArrayOfInt.length >= this.mSpanCount);
    throw new IllegalArgumentException("Provided int[]'s size must be more than or equal to span count. Expected:" + this.mSpanCount + ", array size:" + paramArrayOfInt.length);
    return arrayOfInt;
  }
  
  public int[] findLastVisibleItemPositions(int[] paramArrayOfInt)
  {
    int[] arrayOfInt;
    if (paramArrayOfInt == null) {
      arrayOfInt = new int[this.mSpanCount];
    }
    do
    {
      for (int i = 0; i < this.mSpanCount; i++) {
        arrayOfInt[i] = this.mSpans[i].findLastVisibleItemPosition();
      }
      arrayOfInt = paramArrayOfInt;
    } while (paramArrayOfInt.length >= this.mSpanCount);
    throw new IllegalArgumentException("Provided int[]'s size must be more than or equal to span count. Expected:" + this.mSpanCount + ", array size:" + paramArrayOfInt.length);
    return arrayOfInt;
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
    if (this.mOrientation == 1) {}
    for (int i = this.mSpanCount;; i = super.getColumnCountForAccessibility(paramRecycler, paramState)) {
      return i;
    }
  }
  
  int getFirstChildPosition()
  {
    int i = 0;
    if (getChildCount() == 0) {}
    for (;;)
    {
      return i;
      i = getPosition(getChildAt(0));
    }
  }
  
  public int getGapStrategy()
  {
    return this.mGapStrategy;
  }
  
  int getLastChildPosition()
  {
    int i = getChildCount();
    if (i == 0) {}
    for (i = 0;; i = getPosition(getChildAt(i - 1))) {
      return i;
    }
  }
  
  public int getOrientation()
  {
    return this.mOrientation;
  }
  
  public boolean getReverseLayout()
  {
    return this.mReverseLayout;
  }
  
  public int getRowCountForAccessibility(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    if (this.mOrientation == 0) {}
    for (int i = this.mSpanCount;; i = super.getRowCountForAccessibility(paramRecycler, paramState)) {
      return i;
    }
  }
  
  public int getSpanCount()
  {
    return this.mSpanCount;
  }
  
  View hasGapsToFix()
  {
    int i = getChildCount() - 1;
    BitSet localBitSet = new BitSet(this.mSpanCount);
    localBitSet.set(0, this.mSpanCount, true);
    int j;
    int k;
    label60:
    int m;
    label69:
    int n;
    View localView1;
    LayoutParams localLayoutParams;
    Object localObject;
    if ((this.mOrientation == 1) && (isLayoutRTL()))
    {
      j = 1;
      if (!this.mShouldReverseLayout) {
        break label136;
      }
      k = i;
      i = 0 - 1;
      if (k >= i) {
        break label145;
      }
      m = 1;
      n = k;
      if (n == i) {
        break label371;
      }
      localView1 = getChildAt(n);
      localLayoutParams = (LayoutParams)localView1.getLayoutParams();
      if (!localBitSet.get(localLayoutParams.mSpan.mIndex)) {
        break label163;
      }
      if (!checkSpanForGap(localLayoutParams.mSpan)) {
        break label151;
      }
      localObject = localView1;
    }
    for (;;)
    {
      label128:
      return (View)localObject;
      j = -1;
      break;
      label136:
      k = 0;
      i++;
      break label60;
      label145:
      m = -1;
      break label69;
      label151:
      localBitSet.clear(localLayoutParams.mSpan.mIndex);
      label163:
      if (localLayoutParams.mFullSpan) {}
      label171:
      label254:
      label292:
      label357:
      label359:
      label365:
      label369:
      for (;;)
      {
        n += m;
        break;
        if (n + m != i)
        {
          View localView2 = getChildAt(n + m);
          k = 0;
          int i2;
          if (this.mShouldReverseLayout)
          {
            i1 = this.mPrimaryOrientation.getDecoratedEnd(localView1);
            i2 = this.mPrimaryOrientation.getDecoratedEnd(localView2);
            localObject = localView1;
            if (i1 < i2) {
              break label128;
            }
            if (i1 == i2) {
              k = 1;
            }
            if (k == 0) {
              break label357;
            }
            localObject = (LayoutParams)localView2.getLayoutParams();
            if (localLayoutParams.mSpan.mIndex - ((LayoutParams)localObject).mSpan.mIndex >= 0) {
              break label359;
            }
            k = 1;
            if (j >= 0) {
              break label365;
            }
          }
          for (int i1 = 1;; i1 = 0)
          {
            if (k == i1) {
              break label369;
            }
            localObject = localView1;
            break;
            i1 = this.mPrimaryOrientation.getDecoratedStart(localView1);
            i2 = this.mPrimaryOrientation.getDecoratedStart(localView2);
            localObject = localView1;
            if (i1 > i2) {
              break;
            }
            if (i1 != i2) {
              break label254;
            }
            k = 1;
            break label254;
            break label171;
            k = 0;
            break label292;
          }
        }
      }
      label371:
      localObject = null;
    }
  }
  
  public void invalidateSpanAssignments()
  {
    this.mLazySpanLookup.clear();
    requestLayout();
  }
  
  public boolean isAutoMeasureEnabled()
  {
    if (this.mGapStrategy != 0) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  boolean isLayoutRTL()
  {
    boolean bool = true;
    if (getLayoutDirection() == 1) {}
    for (;;)
    {
      return bool;
      bool = false;
    }
  }
  
  public void offsetChildrenHorizontal(int paramInt)
  {
    super.offsetChildrenHorizontal(paramInt);
    for (int i = 0; i < this.mSpanCount; i++) {
      this.mSpans[i].onOffset(paramInt);
    }
  }
  
  public void offsetChildrenVertical(int paramInt)
  {
    super.offsetChildrenVertical(paramInt);
    for (int i = 0; i < this.mSpanCount; i++) {
      this.mSpans[i].onOffset(paramInt);
    }
  }
  
  public void onDetachedFromWindow(RecyclerView paramRecyclerView, RecyclerView.Recycler paramRecycler)
  {
    super.onDetachedFromWindow(paramRecyclerView, paramRecycler);
    removeCallbacks(this.mCheckForGapsRunnable);
    for (int i = 0; i < this.mSpanCount; i++) {
      this.mSpans[i].clear();
    }
    paramRecyclerView.requestLayout();
  }
  
  public View onFocusSearchFailed(View paramView, int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    if (getChildCount() == 0) {
      paramView = null;
    }
    for (;;)
    {
      return paramView;
      View localView = findContainingItemView(paramView);
      if (localView == null)
      {
        paramView = null;
      }
      else
      {
        resolveShouldLayoutReverse();
        int i = convertFocusDirectionToLayoutDirection(paramInt);
        if (i == Integer.MIN_VALUE)
        {
          paramView = null;
        }
        else
        {
          paramView = (LayoutParams)localView.getLayoutParams();
          boolean bool = paramView.mFullSpan;
          Span localSpan = paramView.mSpan;
          if (i == 1) {}
          for (paramInt = getLastChildPosition();; paramInt = getFirstChildPosition())
          {
            updateLayoutState(paramInt, paramState);
            setLayoutStateDirection(i);
            this.mLayoutState.mCurrentPosition = (this.mLayoutState.mItemDirection + paramInt);
            this.mLayoutState.mAvailable = ((int)(0.33333334F * this.mPrimaryOrientation.getTotalSpace()));
            this.mLayoutState.mStopInFocusable = true;
            this.mLayoutState.mRecycle = false;
            fill(paramRecycler, this.mLayoutState, paramState);
            this.mLastLayoutFromEnd = this.mShouldReverseLayout;
            if (!bool)
            {
              paramRecycler = localSpan.getFocusableViewAfter(paramInt, i);
              if (paramRecycler != null)
              {
                paramView = paramRecycler;
                if (paramRecycler != localView) {
                  break;
                }
              }
            }
            if (!preferLastSpan(i)) {
              break label255;
            }
            for (j = this.mSpanCount - 1;; j--)
            {
              if (j < 0) {
                break label299;
              }
              paramRecycler = this.mSpans[j].getFocusableViewAfter(paramInt, i);
              if (paramRecycler != null)
              {
                paramView = paramRecycler;
                if (paramRecycler != localView) {
                  break;
                }
              }
            }
          }
          label255:
          for (int j = 0;; j++)
          {
            if (j >= this.mSpanCount) {
              break label299;
            }
            paramRecycler = this.mSpans[j].getFocusableViewAfter(paramInt, i);
            if (paramRecycler != null)
            {
              paramView = paramRecycler;
              if (paramRecycler != localView) {
                break;
              }
            }
          }
          label299:
          if (!this.mReverseLayout)
          {
            paramInt = 1;
            label308:
            if (i != -1) {
              break label366;
            }
            j = 1;
            label317:
            if (paramInt != j) {
              break label372;
            }
            paramInt = 1;
            label325:
            if (bool) {
              break label387;
            }
            if (paramInt == 0) {
              break label377;
            }
          }
          label366:
          label372:
          label377:
          for (j = localSpan.findFirstPartiallyVisibleItemPosition();; j = localSpan.findLastPartiallyVisibleItemPosition())
          {
            paramView = findViewByPosition(j);
            if ((paramView == null) || (paramView == localView)) {
              break label387;
            }
            break;
            paramInt = 0;
            break label308;
            j = 0;
            break label317;
            paramInt = 0;
            break label325;
          }
          label387:
          if (preferLastSpan(i))
          {
            label419:
            for (j = this.mSpanCount - 1;; j--)
            {
              if (j < 0) {
                break label545;
              }
              if (j != localSpan.mIndex) {
                break;
              }
            }
            if (paramInt != 0) {}
            for (i = this.mSpans[j].findFirstPartiallyVisibleItemPosition();; i = this.mSpans[j].findLastPartiallyVisibleItemPosition())
            {
              paramView = findViewByPosition(i);
              if ((paramView == null) || (paramView == localView)) {
                break label419;
              }
              break;
            }
          }
          label539:
          for (j = 0;; j++)
          {
            if (j >= this.mSpanCount) {
              break label545;
            }
            if (paramInt != 0) {}
            for (i = this.mSpans[j].findFirstPartiallyVisibleItemPosition();; i = this.mSpans[j].findLastPartiallyVisibleItemPosition())
            {
              paramView = findViewByPosition(i);
              if ((paramView == null) || (paramView == localView)) {
                break label539;
              }
              break;
            }
          }
          label545:
          paramView = null;
        }
      }
    }
  }
  
  public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    super.onInitializeAccessibilityEvent(paramAccessibilityEvent);
    View localView1;
    View localView2;
    if (getChildCount() > 0)
    {
      localView1 = findFirstVisibleItemClosestToStart(false);
      localView2 = findFirstVisibleItemClosestToEnd(false);
      if ((localView1 != null) && (localView2 != null)) {
        break label33;
      }
    }
    for (;;)
    {
      return;
      label33:
      int i = getPosition(localView1);
      int j = getPosition(localView2);
      if (i < j)
      {
        paramAccessibilityEvent.setFromIndex(i);
        paramAccessibilityEvent.setToIndex(j);
      }
      else
      {
        paramAccessibilityEvent.setFromIndex(j);
        paramAccessibilityEvent.setToIndex(i);
      }
    }
  }
  
  public void onInitializeAccessibilityNodeInfoForItem(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
  {
    paramRecycler = paramView.getLayoutParams();
    if (!(paramRecycler instanceof LayoutParams))
    {
      super.onInitializeAccessibilityNodeInfoForItem(paramView, paramAccessibilityNodeInfoCompat);
      return;
    }
    paramRecycler = (LayoutParams)paramRecycler;
    if (this.mOrientation == 0)
    {
      i = paramRecycler.getSpanIndex();
      if (paramRecycler.mFullSpan) {}
      for (j = this.mSpanCount;; j = 1)
      {
        paramAccessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(i, j, -1, -1, paramRecycler.mFullSpan, false));
        break;
      }
    }
    int i = paramRecycler.getSpanIndex();
    if (paramRecycler.mFullSpan) {}
    for (int j = this.mSpanCount;; j = 1)
    {
      paramAccessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(-1, -1, i, j, paramRecycler.mFullSpan, false));
      break;
    }
  }
  
  public void onItemsAdded(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
  {
    handleUpdate(paramInt1, paramInt2, 1);
  }
  
  public void onItemsChanged(RecyclerView paramRecyclerView)
  {
    this.mLazySpanLookup.clear();
    requestLayout();
  }
  
  public void onItemsMoved(RecyclerView paramRecyclerView, int paramInt1, int paramInt2, int paramInt3)
  {
    handleUpdate(paramInt1, paramInt2, 8);
  }
  
  public void onItemsRemoved(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
  {
    handleUpdate(paramInt1, paramInt2, 2);
  }
  
  public void onItemsUpdated(RecyclerView paramRecyclerView, int paramInt1, int paramInt2, Object paramObject)
  {
    handleUpdate(paramInt1, paramInt2, 4);
  }
  
  public void onLayoutChildren(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    onLayoutChildren(paramRecycler, paramState, true);
  }
  
  public void onLayoutCompleted(RecyclerView.State paramState)
  {
    super.onLayoutCompleted(paramState);
    this.mPendingScrollPosition = -1;
    this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
    this.mPendingSavedState = null;
    this.mAnchorInfo.reset();
  }
  
  public void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if ((paramParcelable instanceof SavedState))
    {
      this.mPendingSavedState = ((SavedState)paramParcelable);
      requestLayout();
    }
  }
  
  public Parcelable onSaveInstanceState()
  {
    Object localObject;
    if (this.mPendingSavedState != null) {
      localObject = new SavedState(this.mPendingSavedState);
    }
    for (;;)
    {
      return (Parcelable)localObject;
      SavedState localSavedState = new SavedState();
      localSavedState.mReverseLayout = this.mReverseLayout;
      localSavedState.mAnchorLayoutFromEnd = this.mLastLayoutFromEnd;
      localSavedState.mLastLayoutRTL = this.mLastLayoutRTL;
      int i;
      label120:
      int j;
      label154:
      int k;
      if ((this.mLazySpanLookup != null) && (this.mLazySpanLookup.mData != null))
      {
        localSavedState.mSpanLookup = this.mLazySpanLookup.mData;
        localSavedState.mSpanLookupSize = localSavedState.mSpanLookup.length;
        localSavedState.mFullSpanItems = this.mLazySpanLookup.mFullSpanItems;
        if (getChildCount() <= 0) {
          break label275;
        }
        if (!this.mLastLayoutFromEnd) {
          break label229;
        }
        i = getLastChildPosition();
        localSavedState.mAnchorPosition = i;
        localSavedState.mVisibleAnchorPosition = findFirstVisibleItemPositionInt();
        localSavedState.mSpanOffsetsSize = this.mSpanCount;
        localSavedState.mSpanOffsets = new int[this.mSpanCount];
        j = 0;
        localObject = localSavedState;
        if (j >= this.mSpanCount) {
          continue;
        }
        if (!this.mLastLayoutFromEnd) {
          break label237;
        }
        k = this.mSpans[j].getEndLine(Integer.MIN_VALUE);
        i = k;
        if (k != Integer.MIN_VALUE) {
          i = k - this.mPrimaryOrientation.getEndAfterPadding();
        }
      }
      for (;;)
      {
        localSavedState.mSpanOffsets[j] = i;
        j++;
        break label154;
        localSavedState.mSpanLookupSize = 0;
        break;
        label229:
        i = getFirstChildPosition();
        break label120;
        label237:
        k = this.mSpans[j].getStartLine(Integer.MIN_VALUE);
        i = k;
        if (k != Integer.MIN_VALUE) {
          i = k - this.mPrimaryOrientation.getStartAfterPadding();
        }
      }
      label275:
      localSavedState.mAnchorPosition = -1;
      localSavedState.mVisibleAnchorPosition = -1;
      localSavedState.mSpanOffsetsSize = 0;
      localObject = localSavedState;
    }
  }
  
  public void onScrollStateChanged(int paramInt)
  {
    if (paramInt == 0) {
      checkForGaps();
    }
  }
  
  void prepareLayoutStateForDelta(int paramInt, RecyclerView.State paramState)
  {
    int i;
    if (paramInt > 0) {
      i = 1;
    }
    for (int j = getLastChildPosition();; j = getFirstChildPosition())
    {
      this.mLayoutState.mRecycle = true;
      updateLayoutState(j, paramState);
      setLayoutStateDirection(i);
      this.mLayoutState.mCurrentPosition = (this.mLayoutState.mItemDirection + j);
      this.mLayoutState.mAvailable = Math.abs(paramInt);
      return;
      i = -1;
    }
  }
  
  int scrollBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    if ((getChildCount() == 0) || (paramInt == 0))
    {
      paramInt = 0;
      return paramInt;
    }
    prepareLayoutStateForDelta(paramInt, paramState);
    int i = fill(paramRecycler, this.mLayoutState, paramState);
    if (this.mLayoutState.mAvailable < i) {}
    for (;;)
    {
      this.mPrimaryOrientation.offsetChildren(-paramInt);
      this.mLastLayoutFromEnd = this.mShouldReverseLayout;
      this.mLayoutState.mAvailable = 0;
      recycle(paramRecycler, this.mLayoutState);
      break;
      if (paramInt < 0) {
        paramInt = -i;
      } else {
        paramInt = i;
      }
    }
  }
  
  public int scrollHorizontallyBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    return scrollBy(paramInt, paramRecycler, paramState);
  }
  
  public void scrollToPosition(int paramInt)
  {
    if ((this.mPendingSavedState != null) && (this.mPendingSavedState.mAnchorPosition != paramInt)) {
      this.mPendingSavedState.invalidateAnchorPositionInfo();
    }
    this.mPendingScrollPosition = paramInt;
    this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
    requestLayout();
  }
  
  public void scrollToPositionWithOffset(int paramInt1, int paramInt2)
  {
    if (this.mPendingSavedState != null) {
      this.mPendingSavedState.invalidateAnchorPositionInfo();
    }
    this.mPendingScrollPosition = paramInt1;
    this.mPendingScrollPositionOffset = paramInt2;
    requestLayout();
  }
  
  public int scrollVerticallyBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    return scrollBy(paramInt, paramRecycler, paramState);
  }
  
  public void setGapStrategy(int paramInt)
  {
    assertNotInLayoutOrScroll(null);
    if (paramInt == this.mGapStrategy) {}
    for (;;)
    {
      return;
      if ((paramInt != 0) && (paramInt != 2)) {
        throw new IllegalArgumentException("invalid gap strategy. Must be GAP_HANDLING_NONE or GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS");
      }
      this.mGapStrategy = paramInt;
      requestLayout();
    }
  }
  
  public void setMeasuredDimension(Rect paramRect, int paramInt1, int paramInt2)
  {
    int i = getPaddingLeft() + getPaddingRight();
    int j = getPaddingTop() + getPaddingBottom();
    int k;
    if (this.mOrientation == 1)
    {
      k = chooseSize(paramInt2, paramRect.height() + j, getMinimumHeight());
      paramInt2 = chooseSize(paramInt1, this.mSizePerSpan * this.mSpanCount + i, getMinimumWidth());
      paramInt1 = k;
    }
    for (;;)
    {
      setMeasuredDimension(paramInt2, paramInt1);
      return;
      k = chooseSize(paramInt1, paramRect.width() + i, getMinimumWidth());
      paramInt1 = chooseSize(paramInt2, this.mSizePerSpan * this.mSpanCount + j, getMinimumHeight());
      paramInt2 = k;
    }
  }
  
  public void setOrientation(int paramInt)
  {
    if ((paramInt != 0) && (paramInt != 1)) {
      throw new IllegalArgumentException("invalid orientation.");
    }
    assertNotInLayoutOrScroll(null);
    if (paramInt == this.mOrientation) {}
    for (;;)
    {
      return;
      this.mOrientation = paramInt;
      OrientationHelper localOrientationHelper = this.mPrimaryOrientation;
      this.mPrimaryOrientation = this.mSecondaryOrientation;
      this.mSecondaryOrientation = localOrientationHelper;
      requestLayout();
    }
  }
  
  public void setReverseLayout(boolean paramBoolean)
  {
    assertNotInLayoutOrScroll(null);
    if ((this.mPendingSavedState != null) && (this.mPendingSavedState.mReverseLayout != paramBoolean)) {
      this.mPendingSavedState.mReverseLayout = paramBoolean;
    }
    this.mReverseLayout = paramBoolean;
    requestLayout();
  }
  
  public void setSpanCount(int paramInt)
  {
    assertNotInLayoutOrScroll(null);
    if (paramInt != this.mSpanCount)
    {
      invalidateSpanAssignments();
      this.mSpanCount = paramInt;
      this.mRemainingSpans = new BitSet(this.mSpanCount);
      this.mSpans = new Span[this.mSpanCount];
      for (paramInt = 0; paramInt < this.mSpanCount; paramInt++) {
        this.mSpans[paramInt] = new Span(paramInt);
      }
      requestLayout();
    }
  }
  
  public void smoothScrollToPosition(RecyclerView paramRecyclerView, RecyclerView.State paramState, int paramInt)
  {
    paramRecyclerView = new LinearSmoothScroller(paramRecyclerView.getContext());
    paramRecyclerView.setTargetPosition(paramInt);
    startSmoothScroll(paramRecyclerView);
  }
  
  public boolean supportsPredictiveItemAnimations()
  {
    if (this.mPendingSavedState == null) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  boolean updateAnchorFromPendingData(RecyclerView.State paramState, AnchorInfo paramAnchorInfo)
  {
    boolean bool1 = false;
    boolean bool2 = true;
    if ((paramState.isPreLayout()) || (this.mPendingScrollPosition == -1)) {
      bool1 = false;
    }
    for (;;)
    {
      return bool1;
      if ((this.mPendingScrollPosition < 0) || (this.mPendingScrollPosition >= paramState.getItemCount()))
      {
        this.mPendingScrollPosition = -1;
        this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
        bool1 = false;
      }
      else if ((this.mPendingSavedState == null) || (this.mPendingSavedState.mAnchorPosition == -1) || (this.mPendingSavedState.mSpanOffsetsSize < 1))
      {
        paramState = findViewByPosition(this.mPendingScrollPosition);
        if (paramState != null)
        {
          if (this.mShouldReverseLayout) {}
          for (int i = getLastChildPosition();; i = getFirstChildPosition())
          {
            paramAnchorInfo.mPosition = i;
            if (this.mPendingScrollPositionOffset == Integer.MIN_VALUE) {
              break label206;
            }
            if (!paramAnchorInfo.mLayoutFromEnd) {
              break label175;
            }
            paramAnchorInfo.mOffset = (this.mPrimaryOrientation.getEndAfterPadding() - this.mPendingScrollPositionOffset - this.mPrimaryOrientation.getDecoratedEnd(paramState));
            bool1 = bool2;
            break;
          }
          label175:
          paramAnchorInfo.mOffset = (this.mPrimaryOrientation.getStartAfterPadding() + this.mPendingScrollPositionOffset - this.mPrimaryOrientation.getDecoratedStart(paramState));
          bool1 = bool2;
          continue;
          label206:
          if (this.mPrimaryOrientation.getDecoratedMeasurement(paramState) > this.mPrimaryOrientation.getTotalSpace())
          {
            if (paramAnchorInfo.mLayoutFromEnd) {}
            for (i = this.mPrimaryOrientation.getEndAfterPadding();; i = this.mPrimaryOrientation.getStartAfterPadding())
            {
              paramAnchorInfo.mOffset = i;
              bool1 = bool2;
              break;
            }
          }
          i = this.mPrimaryOrientation.getDecoratedStart(paramState) - this.mPrimaryOrientation.getStartAfterPadding();
          if (i < 0)
          {
            paramAnchorInfo.mOffset = (-i);
            bool1 = bool2;
          }
          else
          {
            i = this.mPrimaryOrientation.getEndAfterPadding() - this.mPrimaryOrientation.getDecoratedEnd(paramState);
            if (i < 0)
            {
              paramAnchorInfo.mOffset = i;
              bool1 = bool2;
            }
            else
            {
              paramAnchorInfo.mOffset = Integer.MIN_VALUE;
              bool1 = bool2;
            }
          }
        }
        else
        {
          paramAnchorInfo.mPosition = this.mPendingScrollPosition;
          if (this.mPendingScrollPositionOffset == Integer.MIN_VALUE)
          {
            if (calculateScrollDirectionForPosition(paramAnchorInfo.mPosition) == 1) {
              bool1 = true;
            }
            paramAnchorInfo.mLayoutFromEnd = bool1;
            paramAnchorInfo.assignCoordinateFromPadding();
          }
          for (;;)
          {
            paramAnchorInfo.mInvalidateOffsets = true;
            bool1 = bool2;
            break;
            paramAnchorInfo.assignCoordinateFromPadding(this.mPendingScrollPositionOffset);
          }
        }
      }
      else
      {
        paramAnchorInfo.mOffset = Integer.MIN_VALUE;
        paramAnchorInfo.mPosition = this.mPendingScrollPosition;
        bool1 = bool2;
      }
    }
  }
  
  void updateAnchorInfoForLayout(RecyclerView.State paramState, AnchorInfo paramAnchorInfo)
  {
    if (updateAnchorFromPendingData(paramState, paramAnchorInfo)) {}
    for (;;)
    {
      return;
      if (!updateAnchorFromChildren(paramState, paramAnchorInfo))
      {
        paramAnchorInfo.assignCoordinateFromPadding();
        paramAnchorInfo.mPosition = 0;
      }
    }
  }
  
  void updateMeasureSpecs(int paramInt)
  {
    this.mSizePerSpan = (paramInt / this.mSpanCount);
    this.mFullSizeSpec = View.MeasureSpec.makeMeasureSpec(paramInt, this.mSecondaryOrientation.getMode());
  }
  
  class AnchorInfo
  {
    boolean mInvalidateOffsets;
    boolean mLayoutFromEnd;
    int mOffset;
    int mPosition;
    int[] mSpanReferenceLines;
    boolean mValid;
    
    AnchorInfo()
    {
      reset();
    }
    
    void assignCoordinateFromPadding()
    {
      if (this.mLayoutFromEnd) {}
      for (int i = StaggeredGridLayoutManager.this.mPrimaryOrientation.getEndAfterPadding();; i = StaggeredGridLayoutManager.this.mPrimaryOrientation.getStartAfterPadding())
      {
        this.mOffset = i;
        return;
      }
    }
    
    void assignCoordinateFromPadding(int paramInt)
    {
      if (this.mLayoutFromEnd) {}
      for (this.mOffset = (StaggeredGridLayoutManager.this.mPrimaryOrientation.getEndAfterPadding() - paramInt);; this.mOffset = (StaggeredGridLayoutManager.this.mPrimaryOrientation.getStartAfterPadding() + paramInt)) {
        return;
      }
    }
    
    void reset()
    {
      this.mPosition = -1;
      this.mOffset = Integer.MIN_VALUE;
      this.mLayoutFromEnd = false;
      this.mInvalidateOffsets = false;
      this.mValid = false;
      if (this.mSpanReferenceLines != null) {
        Arrays.fill(this.mSpanReferenceLines, -1);
      }
    }
    
    void saveSpanReferenceLines(StaggeredGridLayoutManager.Span[] paramArrayOfSpan)
    {
      int i = paramArrayOfSpan.length;
      if ((this.mSpanReferenceLines == null) || (this.mSpanReferenceLines.length < i)) {
        this.mSpanReferenceLines = new int[StaggeredGridLayoutManager.this.mSpans.length];
      }
      for (int j = 0; j < i; j++) {
        this.mSpanReferenceLines[j] = paramArrayOfSpan[j].getStartLine(Integer.MIN_VALUE);
      }
    }
  }
  
  public static class LayoutParams
    extends RecyclerView.LayoutParams
  {
    public static final int INVALID_SPAN_ID = -1;
    boolean mFullSpan;
    StaggeredGridLayoutManager.Span mSpan;
    
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
    
    public final int getSpanIndex()
    {
      if (this.mSpan == null) {}
      for (int i = -1;; i = this.mSpan.mIndex) {
        return i;
      }
    }
    
    public boolean isFullSpan()
    {
      return this.mFullSpan;
    }
    
    public void setFullSpan(boolean paramBoolean)
    {
      this.mFullSpan = paramBoolean;
    }
  }
  
  static class LazySpanLookup
  {
    private static final int MIN_SIZE = 10;
    int[] mData;
    List<FullSpanItem> mFullSpanItems;
    
    private int invalidateFullSpansAfter(int paramInt)
    {
      int i = -1;
      if (this.mFullSpanItems == null)
      {
        paramInt = i;
        return paramInt;
      }
      FullSpanItem localFullSpanItem = getFullSpanItem(paramInt);
      if (localFullSpanItem != null) {
        this.mFullSpanItems.remove(localFullSpanItem);
      }
      int j = -1;
      int k = this.mFullSpanItems.size();
      for (int m = 0;; m++)
      {
        int n = j;
        if (m < k)
        {
          if (((FullSpanItem)this.mFullSpanItems.get(m)).mPosition >= paramInt) {
            n = m;
          }
        }
        else
        {
          paramInt = i;
          if (n == -1) {
            break;
          }
          localFullSpanItem = (FullSpanItem)this.mFullSpanItems.get(n);
          this.mFullSpanItems.remove(n);
          paramInt = localFullSpanItem.mPosition;
          break;
        }
      }
    }
    
    private void offsetFullSpansForAddition(int paramInt1, int paramInt2)
    {
      if (this.mFullSpanItems == null) {
        return;
      }
      int i = this.mFullSpanItems.size() - 1;
      label20:
      FullSpanItem localFullSpanItem;
      if (i >= 0)
      {
        localFullSpanItem = (FullSpanItem)this.mFullSpanItems.get(i);
        if (localFullSpanItem.mPosition >= paramInt1) {
          break label54;
        }
      }
      for (;;)
      {
        i--;
        break label20;
        break;
        label54:
        localFullSpanItem.mPosition += paramInt2;
      }
    }
    
    private void offsetFullSpansForRemoval(int paramInt1, int paramInt2)
    {
      if (this.mFullSpanItems == null) {
        return;
      }
      int i = this.mFullSpanItems.size() - 1;
      label20:
      FullSpanItem localFullSpanItem;
      if (i >= 0)
      {
        localFullSpanItem = (FullSpanItem)this.mFullSpanItems.get(i);
        if (localFullSpanItem.mPosition >= paramInt1) {
          break label54;
        }
      }
      for (;;)
      {
        i--;
        break label20;
        break;
        label54:
        if (localFullSpanItem.mPosition < paramInt1 + paramInt2) {
          this.mFullSpanItems.remove(i);
        } else {
          localFullSpanItem.mPosition -= paramInt2;
        }
      }
    }
    
    public void addFullSpanItem(FullSpanItem paramFullSpanItem)
    {
      if (this.mFullSpanItems == null) {
        this.mFullSpanItems = new ArrayList();
      }
      int i = this.mFullSpanItems.size();
      int j = 0;
      if (j < i)
      {
        FullSpanItem localFullSpanItem = (FullSpanItem)this.mFullSpanItems.get(j);
        if (localFullSpanItem.mPosition == paramFullSpanItem.mPosition) {
          this.mFullSpanItems.remove(j);
        }
        if (localFullSpanItem.mPosition >= paramFullSpanItem.mPosition) {
          this.mFullSpanItems.add(j, paramFullSpanItem);
        }
      }
      for (;;)
      {
        return;
        j++;
        break;
        this.mFullSpanItems.add(paramFullSpanItem);
      }
    }
    
    void clear()
    {
      if (this.mData != null) {
        Arrays.fill(this.mData, -1);
      }
      this.mFullSpanItems = null;
    }
    
    void ensureSize(int paramInt)
    {
      if (this.mData == null)
      {
        this.mData = new int[Math.max(paramInt, 10) + 1];
        Arrays.fill(this.mData, -1);
      }
      for (;;)
      {
        return;
        if (paramInt >= this.mData.length)
        {
          int[] arrayOfInt = this.mData;
          this.mData = new int[sizeForPosition(paramInt)];
          System.arraycopy(arrayOfInt, 0, this.mData, 0, arrayOfInt.length);
          Arrays.fill(this.mData, arrayOfInt.length, this.mData.length, -1);
        }
      }
    }
    
    int forceInvalidateAfter(int paramInt)
    {
      if (this.mFullSpanItems != null) {
        for (int i = this.mFullSpanItems.size() - 1; i >= 0; i--) {
          if (((FullSpanItem)this.mFullSpanItems.get(i)).mPosition >= paramInt) {
            this.mFullSpanItems.remove(i);
          }
        }
      }
      return invalidateAfter(paramInt);
    }
    
    public FullSpanItem getFirstFullSpanItemInRange(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
    {
      Object localObject;
      if (this.mFullSpanItems == null) {
        localObject = null;
      }
      for (;;)
      {
        return (FullSpanItem)localObject;
        int i = this.mFullSpanItems.size();
        for (int j = 0;; j++)
        {
          if (j >= i) {
            break label118;
          }
          FullSpanItem localFullSpanItem = (FullSpanItem)this.mFullSpanItems.get(j);
          if (localFullSpanItem.mPosition >= paramInt2)
          {
            localObject = null;
            break;
          }
          if (localFullSpanItem.mPosition >= paramInt1)
          {
            localObject = localFullSpanItem;
            if (paramInt3 == 0) {
              break;
            }
            localObject = localFullSpanItem;
            if (localFullSpanItem.mGapDir == paramInt3) {
              break;
            }
            if (paramBoolean)
            {
              localObject = localFullSpanItem;
              if (localFullSpanItem.mHasUnwantedGapAfter) {
                break;
              }
            }
          }
        }
        label118:
        localObject = null;
      }
    }
    
    public FullSpanItem getFullSpanItem(int paramInt)
    {
      Object localObject;
      if (this.mFullSpanItems == null) {
        localObject = null;
      }
      for (;;)
      {
        return (FullSpanItem)localObject;
        for (int i = this.mFullSpanItems.size() - 1;; i--)
        {
          if (i < 0) {
            break label60;
          }
          FullSpanItem localFullSpanItem = (FullSpanItem)this.mFullSpanItems.get(i);
          localObject = localFullSpanItem;
          if (localFullSpanItem.mPosition == paramInt) {
            break;
          }
        }
        label60:
        localObject = null;
      }
    }
    
    int getSpan(int paramInt)
    {
      if ((this.mData == null) || (paramInt >= this.mData.length)) {}
      for (paramInt = -1;; paramInt = this.mData[paramInt]) {
        return paramInt;
      }
    }
    
    int invalidateAfter(int paramInt)
    {
      int i = -1;
      if (this.mData == null) {}
      for (;;)
      {
        return i;
        if (paramInt < this.mData.length)
        {
          i = invalidateFullSpansAfter(paramInt);
          if (i == -1)
          {
            Arrays.fill(this.mData, paramInt, this.mData.length, -1);
            i = this.mData.length;
          }
          else
          {
            Arrays.fill(this.mData, paramInt, i + 1, -1);
            i++;
          }
        }
      }
    }
    
    void offsetForAddition(int paramInt1, int paramInt2)
    {
      if ((this.mData == null) || (paramInt1 >= this.mData.length)) {}
      for (;;)
      {
        return;
        ensureSize(paramInt1 + paramInt2);
        System.arraycopy(this.mData, paramInt1, this.mData, paramInt1 + paramInt2, this.mData.length - paramInt1 - paramInt2);
        Arrays.fill(this.mData, paramInt1, paramInt1 + paramInt2, -1);
        offsetFullSpansForAddition(paramInt1, paramInt2);
      }
    }
    
    void offsetForRemoval(int paramInt1, int paramInt2)
    {
      if ((this.mData == null) || (paramInt1 >= this.mData.length)) {}
      for (;;)
      {
        return;
        ensureSize(paramInt1 + paramInt2);
        System.arraycopy(this.mData, paramInt1 + paramInt2, this.mData, paramInt1, this.mData.length - paramInt1 - paramInt2);
        Arrays.fill(this.mData, this.mData.length - paramInt2, this.mData.length, -1);
        offsetFullSpansForRemoval(paramInt1, paramInt2);
      }
    }
    
    void setSpan(int paramInt, StaggeredGridLayoutManager.Span paramSpan)
    {
      ensureSize(paramInt);
      this.mData[paramInt] = paramSpan.mIndex;
    }
    
    int sizeForPosition(int paramInt)
    {
      int i = this.mData.length;
      while (i <= paramInt) {
        i *= 2;
      }
      return i;
    }
    
    static class FullSpanItem
      implements Parcelable
    {
      public static final Parcelable.Creator<FullSpanItem> CREATOR = new Parcelable.Creator()
      {
        public StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem createFromParcel(Parcel paramAnonymousParcel)
        {
          return new StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem(paramAnonymousParcel);
        }
        
        public StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem[] newArray(int paramAnonymousInt)
        {
          return new StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem[paramAnonymousInt];
        }
      };
      int mGapDir;
      int[] mGapPerSpan;
      boolean mHasUnwantedGapAfter;
      int mPosition;
      
      FullSpanItem() {}
      
      FullSpanItem(Parcel paramParcel)
      {
        this.mPosition = paramParcel.readInt();
        this.mGapDir = paramParcel.readInt();
        if (paramParcel.readInt() == 1) {}
        for (;;)
        {
          this.mHasUnwantedGapAfter = bool;
          int i = paramParcel.readInt();
          if (i > 0)
          {
            this.mGapPerSpan = new int[i];
            paramParcel.readIntArray(this.mGapPerSpan);
          }
          return;
          bool = false;
        }
      }
      
      public int describeContents()
      {
        return 0;
      }
      
      int getGapForSpan(int paramInt)
      {
        if (this.mGapPerSpan == null) {}
        for (paramInt = 0;; paramInt = this.mGapPerSpan[paramInt]) {
          return paramInt;
        }
      }
      
      public String toString()
      {
        return "FullSpanItem{mPosition=" + this.mPosition + ", mGapDir=" + this.mGapDir + ", mHasUnwantedGapAfter=" + this.mHasUnwantedGapAfter + ", mGapPerSpan=" + Arrays.toString(this.mGapPerSpan) + '}';
      }
      
      public void writeToParcel(Parcel paramParcel, int paramInt)
      {
        paramParcel.writeInt(this.mPosition);
        paramParcel.writeInt(this.mGapDir);
        if (this.mHasUnwantedGapAfter)
        {
          paramInt = 1;
          paramParcel.writeInt(paramInt);
          if ((this.mGapPerSpan == null) || (this.mGapPerSpan.length <= 0)) {
            break label68;
          }
          paramParcel.writeInt(this.mGapPerSpan.length);
          paramParcel.writeIntArray(this.mGapPerSpan);
        }
        for (;;)
        {
          return;
          paramInt = 0;
          break;
          label68:
          paramParcel.writeInt(0);
        }
      }
    }
  }
  
  public static class SavedState
    implements Parcelable
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public StaggeredGridLayoutManager.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new StaggeredGridLayoutManager.SavedState(paramAnonymousParcel);
      }
      
      public StaggeredGridLayoutManager.SavedState[] newArray(int paramAnonymousInt)
      {
        return new StaggeredGridLayoutManager.SavedState[paramAnonymousInt];
      }
    };
    boolean mAnchorLayoutFromEnd;
    int mAnchorPosition;
    List<StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem> mFullSpanItems;
    boolean mLastLayoutRTL;
    boolean mReverseLayout;
    int[] mSpanLookup;
    int mSpanLookupSize;
    int[] mSpanOffsets;
    int mSpanOffsetsSize;
    int mVisibleAnchorPosition;
    
    public SavedState() {}
    
    SavedState(Parcel paramParcel)
    {
      this.mAnchorPosition = paramParcel.readInt();
      this.mVisibleAnchorPosition = paramParcel.readInt();
      this.mSpanOffsetsSize = paramParcel.readInt();
      if (this.mSpanOffsetsSize > 0)
      {
        this.mSpanOffsets = new int[this.mSpanOffsetsSize];
        paramParcel.readIntArray(this.mSpanOffsets);
      }
      this.mSpanLookupSize = paramParcel.readInt();
      if (this.mSpanLookupSize > 0)
      {
        this.mSpanLookup = new int[this.mSpanLookupSize];
        paramParcel.readIntArray(this.mSpanLookup);
      }
      if (paramParcel.readInt() == 1)
      {
        bool2 = true;
        this.mReverseLayout = bool2;
        if (paramParcel.readInt() != 1) {
          break label152;
        }
        bool2 = true;
        label113:
        this.mAnchorLayoutFromEnd = bool2;
        if (paramParcel.readInt() != 1) {
          break label157;
        }
      }
      label152:
      label157:
      for (boolean bool2 = bool1;; bool2 = false)
      {
        this.mLastLayoutRTL = bool2;
        this.mFullSpanItems = paramParcel.readArrayList(StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem.class.getClassLoader());
        return;
        bool2 = false;
        break;
        bool2 = false;
        break label113;
      }
    }
    
    public SavedState(SavedState paramSavedState)
    {
      this.mSpanOffsetsSize = paramSavedState.mSpanOffsetsSize;
      this.mAnchorPosition = paramSavedState.mAnchorPosition;
      this.mVisibleAnchorPosition = paramSavedState.mVisibleAnchorPosition;
      this.mSpanOffsets = paramSavedState.mSpanOffsets;
      this.mSpanLookupSize = paramSavedState.mSpanLookupSize;
      this.mSpanLookup = paramSavedState.mSpanLookup;
      this.mReverseLayout = paramSavedState.mReverseLayout;
      this.mAnchorLayoutFromEnd = paramSavedState.mAnchorLayoutFromEnd;
      this.mLastLayoutRTL = paramSavedState.mLastLayoutRTL;
      this.mFullSpanItems = paramSavedState.mFullSpanItems;
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    void invalidateAnchorPositionInfo()
    {
      this.mSpanOffsets = null;
      this.mSpanOffsetsSize = 0;
      this.mAnchorPosition = -1;
      this.mVisibleAnchorPosition = -1;
    }
    
    void invalidateSpanInfo()
    {
      this.mSpanOffsets = null;
      this.mSpanOffsetsSize = 0;
      this.mSpanLookupSize = 0;
      this.mSpanLookup = null;
      this.mFullSpanItems = null;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      int i = 1;
      paramParcel.writeInt(this.mAnchorPosition);
      paramParcel.writeInt(this.mVisibleAnchorPosition);
      paramParcel.writeInt(this.mSpanOffsetsSize);
      if (this.mSpanOffsetsSize > 0) {
        paramParcel.writeIntArray(this.mSpanOffsets);
      }
      paramParcel.writeInt(this.mSpanLookupSize);
      if (this.mSpanLookupSize > 0) {
        paramParcel.writeIntArray(this.mSpanLookup);
      }
      if (this.mReverseLayout)
      {
        paramInt = 1;
        paramParcel.writeInt(paramInt);
        if (!this.mAnchorLayoutFromEnd) {
          break label120;
        }
        paramInt = 1;
        label87:
        paramParcel.writeInt(paramInt);
        if (!this.mLastLayoutRTL) {
          break label125;
        }
      }
      label120:
      label125:
      for (paramInt = i;; paramInt = 0)
      {
        paramParcel.writeInt(paramInt);
        paramParcel.writeList(this.mFullSpanItems);
        return;
        paramInt = 0;
        break;
        paramInt = 0;
        break label87;
      }
    }
  }
  
  class Span
  {
    static final int INVALID_LINE = Integer.MIN_VALUE;
    int mCachedEnd = Integer.MIN_VALUE;
    int mCachedStart = Integer.MIN_VALUE;
    int mDeletedSize = 0;
    final int mIndex;
    ArrayList<View> mViews = new ArrayList();
    
    Span(int paramInt)
    {
      this.mIndex = paramInt;
    }
    
    void appendToSpan(View paramView)
    {
      StaggeredGridLayoutManager.LayoutParams localLayoutParams = getLayoutParams(paramView);
      localLayoutParams.mSpan = this;
      this.mViews.add(paramView);
      this.mCachedEnd = Integer.MIN_VALUE;
      if (this.mViews.size() == 1) {
        this.mCachedStart = Integer.MIN_VALUE;
      }
      if ((localLayoutParams.isItemRemoved()) || (localLayoutParams.isItemChanged())) {
        this.mDeletedSize += StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedMeasurement(paramView);
      }
    }
    
    void cacheReferenceLineAndClear(boolean paramBoolean, int paramInt)
    {
      int i;
      if (paramBoolean)
      {
        i = getEndLine(Integer.MIN_VALUE);
        clear();
        if (i != Integer.MIN_VALUE) {
          break label32;
        }
      }
      for (;;)
      {
        return;
        i = getStartLine(Integer.MIN_VALUE);
        break;
        label32:
        if (((!paramBoolean) || (i >= StaggeredGridLayoutManager.this.mPrimaryOrientation.getEndAfterPadding())) && ((paramBoolean) || (i <= StaggeredGridLayoutManager.this.mPrimaryOrientation.getStartAfterPadding())))
        {
          int j = i;
          if (paramInt != Integer.MIN_VALUE) {
            j = i + paramInt;
          }
          this.mCachedEnd = j;
          this.mCachedStart = j;
        }
      }
    }
    
    void calculateCachedEnd()
    {
      View localView = (View)this.mViews.get(this.mViews.size() - 1);
      Object localObject = getLayoutParams(localView);
      this.mCachedEnd = StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedEnd(localView);
      if (((StaggeredGridLayoutManager.LayoutParams)localObject).mFullSpan)
      {
        localObject = StaggeredGridLayoutManager.this.mLazySpanLookup.getFullSpanItem(((StaggeredGridLayoutManager.LayoutParams)localObject).getViewLayoutPosition());
        if ((localObject != null) && (((StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem)localObject).mGapDir == 1)) {
          this.mCachedEnd += ((StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem)localObject).getGapForSpan(this.mIndex);
        }
      }
    }
    
    void calculateCachedStart()
    {
      Object localObject = (View)this.mViews.get(0);
      StaggeredGridLayoutManager.LayoutParams localLayoutParams = getLayoutParams((View)localObject);
      this.mCachedStart = StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedStart((View)localObject);
      if (localLayoutParams.mFullSpan)
      {
        localObject = StaggeredGridLayoutManager.this.mLazySpanLookup.getFullSpanItem(localLayoutParams.getViewLayoutPosition());
        if ((localObject != null) && (((StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem)localObject).mGapDir == -1)) {
          this.mCachedStart -= ((StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem)localObject).getGapForSpan(this.mIndex);
        }
      }
    }
    
    void clear()
    {
      this.mViews.clear();
      invalidateCache();
      this.mDeletedSize = 0;
    }
    
    public int findFirstCompletelyVisibleItemPosition()
    {
      if (StaggeredGridLayoutManager.this.mReverseLayout) {}
      for (int i = findOneVisibleChild(this.mViews.size() - 1, -1, true);; i = findOneVisibleChild(0, this.mViews.size(), true)) {
        return i;
      }
    }
    
    public int findFirstPartiallyVisibleItemPosition()
    {
      if (StaggeredGridLayoutManager.this.mReverseLayout) {}
      for (int i = findOnePartiallyVisibleChild(this.mViews.size() - 1, -1, true);; i = findOnePartiallyVisibleChild(0, this.mViews.size(), true)) {
        return i;
      }
    }
    
    public int findFirstVisibleItemPosition()
    {
      if (StaggeredGridLayoutManager.this.mReverseLayout) {}
      for (int i = findOneVisibleChild(this.mViews.size() - 1, -1, false);; i = findOneVisibleChild(0, this.mViews.size(), false)) {
        return i;
      }
    }
    
    public int findLastCompletelyVisibleItemPosition()
    {
      if (StaggeredGridLayoutManager.this.mReverseLayout) {}
      for (int i = findOneVisibleChild(0, this.mViews.size(), true);; i = findOneVisibleChild(this.mViews.size() - 1, -1, true)) {
        return i;
      }
    }
    
    public int findLastPartiallyVisibleItemPosition()
    {
      if (StaggeredGridLayoutManager.this.mReverseLayout) {}
      for (int i = findOnePartiallyVisibleChild(0, this.mViews.size(), true);; i = findOnePartiallyVisibleChild(this.mViews.size() - 1, -1, true)) {
        return i;
      }
    }
    
    public int findLastVisibleItemPosition()
    {
      if (StaggeredGridLayoutManager.this.mReverseLayout) {}
      for (int i = findOneVisibleChild(0, this.mViews.size(), false);; i = findOneVisibleChild(this.mViews.size() - 1, -1, false)) {
        return i;
      }
    }
    
    int findOnePartiallyOrCompletelyVisibleChild(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
    {
      int i = StaggeredGridLayoutManager.this.mPrimaryOrientation.getStartAfterPadding();
      int j = StaggeredGridLayoutManager.this.mPrimaryOrientation.getEndAfterPadding();
      int k;
      int m;
      label35:
      View localView;
      int n;
      int i1;
      label97:
      int i2;
      if (paramInt2 > paramInt1)
      {
        k = 1;
        m = paramInt1;
        if (m == paramInt2) {
          break label264;
        }
        localView = (View)this.mViews.get(m);
        n = StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedStart(localView);
        i1 = StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedEnd(localView);
        if (!paramBoolean3) {
          break label167;
        }
        if (n > j) {
          break label162;
        }
        paramInt1 = 1;
        if (!paramBoolean3) {
          break label190;
        }
        if (i1 < i) {
          break label184;
        }
        i2 = 1;
        label112:
        if ((paramInt1 == 0) || (i2 == 0)) {
          break label254;
        }
        if ((!paramBoolean1) || (!paramBoolean2)) {
          break label209;
        }
        if ((n < i) || (i1 > j)) {
          break label254;
        }
        paramInt1 = StaggeredGridLayoutManager.this.getPosition(localView);
      }
      for (;;)
      {
        return paramInt1;
        k = -1;
        break;
        label162:
        paramInt1 = 0;
        break label97;
        label167:
        if (n < j)
        {
          paramInt1 = 1;
          break label97;
        }
        paramInt1 = 0;
        break label97;
        label184:
        i2 = 0;
        break label112;
        label190:
        if (i1 > i)
        {
          i2 = 1;
          break label112;
        }
        i2 = 0;
        break label112;
        label209:
        if (paramBoolean2)
        {
          paramInt1 = StaggeredGridLayoutManager.this.getPosition(localView);
        }
        else if ((n < i) || (i1 > j))
        {
          paramInt1 = StaggeredGridLayoutManager.this.getPosition(localView);
        }
        else
        {
          label254:
          m += k;
          break label35;
          label264:
          paramInt1 = -1;
        }
      }
    }
    
    int findOnePartiallyVisibleChild(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      return findOnePartiallyOrCompletelyVisibleChild(paramInt1, paramInt2, false, false, paramBoolean);
    }
    
    int findOneVisibleChild(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      return findOnePartiallyOrCompletelyVisibleChild(paramInt1, paramInt2, paramBoolean, true, false);
    }
    
    public int getDeletedSize()
    {
      return this.mDeletedSize;
    }
    
    int getEndLine()
    {
      if (this.mCachedEnd != Integer.MIN_VALUE) {}
      for (int i = this.mCachedEnd;; i = this.mCachedEnd)
      {
        return i;
        calculateCachedEnd();
      }
    }
    
    int getEndLine(int paramInt)
    {
      if (this.mCachedEnd != Integer.MIN_VALUE) {
        paramInt = this.mCachedEnd;
      }
      for (;;)
      {
        return paramInt;
        if (this.mViews.size() != 0)
        {
          calculateCachedEnd();
          paramInt = this.mCachedEnd;
        }
      }
    }
    
    public View getFocusableViewAfter(int paramInt1, int paramInt2)
    {
      Object localObject1 = null;
      Object localObject2 = null;
      View localView;
      if (paramInt2 == -1)
      {
        int i = this.mViews.size();
        for (paramInt2 = 0;; paramInt2++)
        {
          localObject1 = localObject2;
          if (paramInt2 < i)
          {
            localView = (View)this.mViews.get(paramInt2);
            if (StaggeredGridLayoutManager.this.mReverseLayout)
            {
              localObject1 = localObject2;
              if (StaggeredGridLayoutManager.this.getPosition(localView) <= paramInt1) {}
            }
            else
            {
              if ((StaggeredGridLayoutManager.this.mReverseLayout) || (StaggeredGridLayoutManager.this.getPosition(localView) < paramInt1)) {
                break label97;
              }
              localObject1 = localObject2;
            }
          }
          label97:
          do
          {
            return (View)localObject1;
            localObject1 = localObject2;
          } while (!localView.hasFocusable());
          localObject2 = localView;
        }
      }
      paramInt2 = this.mViews.size() - 1;
      localObject2 = localObject1;
      for (;;)
      {
        localObject1 = localObject2;
        if (paramInt2 < 0) {
          break;
        }
        localView = (View)this.mViews.get(paramInt2);
        if (StaggeredGridLayoutManager.this.mReverseLayout)
        {
          localObject1 = localObject2;
          if (StaggeredGridLayoutManager.this.getPosition(localView) >= paramInt1) {
            break;
          }
        }
        if (!StaggeredGridLayoutManager.this.mReverseLayout)
        {
          localObject1 = localObject2;
          if (StaggeredGridLayoutManager.this.getPosition(localView) <= paramInt1) {
            break;
          }
        }
        localObject1 = localObject2;
        if (!localView.hasFocusable()) {
          break;
        }
        localObject2 = localView;
        paramInt2--;
      }
    }
    
    StaggeredGridLayoutManager.LayoutParams getLayoutParams(View paramView)
    {
      return (StaggeredGridLayoutManager.LayoutParams)paramView.getLayoutParams();
    }
    
    int getStartLine()
    {
      if (this.mCachedStart != Integer.MIN_VALUE) {}
      for (int i = this.mCachedStart;; i = this.mCachedStart)
      {
        return i;
        calculateCachedStart();
      }
    }
    
    int getStartLine(int paramInt)
    {
      if (this.mCachedStart != Integer.MIN_VALUE) {
        paramInt = this.mCachedStart;
      }
      for (;;)
      {
        return paramInt;
        if (this.mViews.size() != 0)
        {
          calculateCachedStart();
          paramInt = this.mCachedStart;
        }
      }
    }
    
    void invalidateCache()
    {
      this.mCachedStart = Integer.MIN_VALUE;
      this.mCachedEnd = Integer.MIN_VALUE;
    }
    
    void onOffset(int paramInt)
    {
      if (this.mCachedStart != Integer.MIN_VALUE) {
        this.mCachedStart += paramInt;
      }
      if (this.mCachedEnd != Integer.MIN_VALUE) {
        this.mCachedEnd += paramInt;
      }
    }
    
    void popEnd()
    {
      int i = this.mViews.size();
      View localView = (View)this.mViews.remove(i - 1);
      StaggeredGridLayoutManager.LayoutParams localLayoutParams = getLayoutParams(localView);
      localLayoutParams.mSpan = null;
      if ((localLayoutParams.isItemRemoved()) || (localLayoutParams.isItemChanged())) {
        this.mDeletedSize -= StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedMeasurement(localView);
      }
      if (i == 1) {
        this.mCachedStart = Integer.MIN_VALUE;
      }
      this.mCachedEnd = Integer.MIN_VALUE;
    }
    
    void popStart()
    {
      View localView = (View)this.mViews.remove(0);
      StaggeredGridLayoutManager.LayoutParams localLayoutParams = getLayoutParams(localView);
      localLayoutParams.mSpan = null;
      if (this.mViews.size() == 0) {
        this.mCachedEnd = Integer.MIN_VALUE;
      }
      if ((localLayoutParams.isItemRemoved()) || (localLayoutParams.isItemChanged())) {
        this.mDeletedSize -= StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedMeasurement(localView);
      }
      this.mCachedStart = Integer.MIN_VALUE;
    }
    
    void prependToSpan(View paramView)
    {
      StaggeredGridLayoutManager.LayoutParams localLayoutParams = getLayoutParams(paramView);
      localLayoutParams.mSpan = this;
      this.mViews.add(0, paramView);
      this.mCachedStart = Integer.MIN_VALUE;
      if (this.mViews.size() == 1) {
        this.mCachedEnd = Integer.MIN_VALUE;
      }
      if ((localLayoutParams.isItemRemoved()) || (localLayoutParams.isItemChanged())) {
        this.mDeletedSize += StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedMeasurement(paramView);
      }
    }
    
    void setLine(int paramInt)
    {
      this.mCachedStart = paramInt;
      this.mCachedEnd = paramInt;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/support/widget/StaggeredGridLayoutManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */