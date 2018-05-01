package org.telegram.messenger.support.widget;

import android.content.Context;
import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import java.util.List;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.ViewDropHandler;

public class LinearLayoutManager
  extends RecyclerView.LayoutManager
  implements RecyclerView.SmoothScroller.ScrollVectorProvider, ItemTouchHelper.ViewDropHandler
{
  static final boolean DEBUG = false;
  public static final int HORIZONTAL = 0;
  public static final int INVALID_OFFSET = Integer.MIN_VALUE;
  private static final float MAX_SCROLL_FACTOR = 0.33333334F;
  private static final String TAG = "LinearLayoutManager";
  public static final int VERTICAL = 1;
  final AnchorInfo mAnchorInfo = new AnchorInfo();
  private int mInitialPrefetchItemCount = 2;
  private boolean mLastStackFromEnd;
  private final LayoutChunkResult mLayoutChunkResult = new LayoutChunkResult();
  private LayoutState mLayoutState;
  int mOrientation = 1;
  OrientationHelper mOrientationHelper;
  SavedState mPendingSavedState = null;
  int mPendingScrollPosition = -1;
  boolean mPendingScrollPositionBottom = true;
  int mPendingScrollPositionOffset = Integer.MIN_VALUE;
  private boolean mRecycleChildrenOnDetach;
  private boolean mReverseLayout = false;
  boolean mShouldReverseLayout = false;
  private boolean mSmoothScrollbarEnabled = true;
  private boolean mStackFromEnd = false;
  
  public LinearLayoutManager(Context paramContext)
  {
    this(paramContext, 1, false);
  }
  
  public LinearLayoutManager(Context paramContext, int paramInt, boolean paramBoolean)
  {
    setOrientation(paramInt);
    setReverseLayout(paramBoolean);
  }
  
  private int computeScrollExtent(RecyclerView.State paramState)
  {
    boolean bool1 = false;
    int i = 0;
    if (getChildCount() == 0) {
      return i;
    }
    ensureLayoutState();
    OrientationHelper localOrientationHelper = this.mOrientationHelper;
    if (!this.mSmoothScrollbarEnabled) {}
    for (boolean bool2 = true;; bool2 = false)
    {
      View localView = findFirstVisibleChildClosestToStart(bool2, true);
      bool2 = bool1;
      if (!this.mSmoothScrollbarEnabled) {
        bool2 = true;
      }
      i = ScrollbarHelper.computeScrollExtent(paramState, localOrientationHelper, localView, findFirstVisibleChildClosestToEnd(bool2, true), this, this.mSmoothScrollbarEnabled);
      break;
    }
  }
  
  private int computeScrollOffset(RecyclerView.State paramState)
  {
    boolean bool1 = false;
    int i = 0;
    if (getChildCount() == 0) {
      return i;
    }
    ensureLayoutState();
    OrientationHelper localOrientationHelper = this.mOrientationHelper;
    if (!this.mSmoothScrollbarEnabled) {}
    for (boolean bool2 = true;; bool2 = false)
    {
      View localView = findFirstVisibleChildClosestToStart(bool2, true);
      bool2 = bool1;
      if (!this.mSmoothScrollbarEnabled) {
        bool2 = true;
      }
      i = ScrollbarHelper.computeScrollOffset(paramState, localOrientationHelper, localView, findFirstVisibleChildClosestToEnd(bool2, true), this, this.mSmoothScrollbarEnabled, this.mShouldReverseLayout);
      break;
    }
  }
  
  private int computeScrollRange(RecyclerView.State paramState)
  {
    boolean bool1 = false;
    int i = 0;
    if (getChildCount() == 0) {
      return i;
    }
    ensureLayoutState();
    OrientationHelper localOrientationHelper = this.mOrientationHelper;
    if (!this.mSmoothScrollbarEnabled) {}
    for (boolean bool2 = true;; bool2 = false)
    {
      View localView = findFirstVisibleChildClosestToStart(bool2, true);
      bool2 = bool1;
      if (!this.mSmoothScrollbarEnabled) {
        bool2 = true;
      }
      i = ScrollbarHelper.computeScrollRange(paramState, localOrientationHelper, localView, findFirstVisibleChildClosestToEnd(bool2, true), this, this.mSmoothScrollbarEnabled);
      break;
    }
  }
  
  private View findFirstPartiallyOrCompletelyInvisibleChild(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    return findOnePartiallyOrCompletelyInvisibleChild(0, getChildCount());
  }
  
  private View findFirstReferenceChild(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    return findReferenceChild(paramRecycler, paramState, 0, getChildCount(), paramState.getItemCount());
  }
  
  private View findFirstVisibleChildClosestToEnd(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.mShouldReverseLayout) {}
    for (View localView = findOneVisibleChild(0, getChildCount(), paramBoolean1, paramBoolean2);; localView = findOneVisibleChild(getChildCount() - 1, -1, paramBoolean1, paramBoolean2)) {
      return localView;
    }
  }
  
  private View findFirstVisibleChildClosestToStart(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.mShouldReverseLayout) {}
    for (View localView = findOneVisibleChild(getChildCount() - 1, -1, paramBoolean1, paramBoolean2);; localView = findOneVisibleChild(0, getChildCount(), paramBoolean1, paramBoolean2)) {
      return localView;
    }
  }
  
  private View findLastPartiallyOrCompletelyInvisibleChild(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    return findOnePartiallyOrCompletelyInvisibleChild(getChildCount() - 1, -1);
  }
  
  private View findLastReferenceChild(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    return findReferenceChild(paramRecycler, paramState, getChildCount() - 1, -1, paramState.getItemCount());
  }
  
  private View findPartiallyOrCompletelyInvisibleChildClosestToEnd(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    if (this.mShouldReverseLayout) {}
    for (paramRecycler = findFirstPartiallyOrCompletelyInvisibleChild(paramRecycler, paramState);; paramRecycler = findLastPartiallyOrCompletelyInvisibleChild(paramRecycler, paramState)) {
      return paramRecycler;
    }
  }
  
  private View findPartiallyOrCompletelyInvisibleChildClosestToStart(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    if (this.mShouldReverseLayout) {}
    for (paramRecycler = findLastPartiallyOrCompletelyInvisibleChild(paramRecycler, paramState);; paramRecycler = findFirstPartiallyOrCompletelyInvisibleChild(paramRecycler, paramState)) {
      return paramRecycler;
    }
  }
  
  private View findReferenceChildClosestToEnd(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    if (this.mShouldReverseLayout) {}
    for (paramRecycler = findFirstReferenceChild(paramRecycler, paramState);; paramRecycler = findLastReferenceChild(paramRecycler, paramState)) {
      return paramRecycler;
    }
  }
  
  private View findReferenceChildClosestToStart(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    if (this.mShouldReverseLayout) {}
    for (paramRecycler = findLastReferenceChild(paramRecycler, paramState);; paramRecycler = findFirstReferenceChild(paramRecycler, paramState)) {
      return paramRecycler;
    }
  }
  
  private int fixLayoutEndGap(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, boolean paramBoolean)
  {
    int i = this.mOrientationHelper.getEndAfterPadding() - paramInt;
    if (i > 0)
    {
      i = -scrollBy(-i, paramRecycler, paramState);
      if (!paramBoolean) {
        break label70;
      }
      paramInt = this.mOrientationHelper.getEndAfterPadding() - (paramInt + i);
      if (paramInt <= 0) {
        break label70;
      }
      this.mOrientationHelper.offsetChildren(paramInt);
      paramInt += i;
    }
    for (;;)
    {
      return paramInt;
      paramInt = 0;
      continue;
      label70:
      paramInt = i;
    }
  }
  
  private int fixLayoutStartGap(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, boolean paramBoolean)
  {
    int i = paramInt - this.mOrientationHelper.getStartAfterPadding();
    if (i > 0)
    {
      i = -scrollBy(i, paramRecycler, paramState);
      if (!paramBoolean) {
        break label70;
      }
      paramInt = paramInt + i - this.mOrientationHelper.getStartAfterPadding();
      if (paramInt <= 0) {
        break label70;
      }
      this.mOrientationHelper.offsetChildren(-paramInt);
      paramInt = i - paramInt;
    }
    for (;;)
    {
      return paramInt;
      paramInt = 0;
      continue;
      label70:
      paramInt = i;
    }
  }
  
  private View getChildClosestToEnd()
  {
    if (this.mShouldReverseLayout) {}
    for (int i = 0;; i = getChildCount() - 1) {
      return getChildAt(i);
    }
  }
  
  private View getChildClosestToStart()
  {
    if (this.mShouldReverseLayout) {}
    for (int i = getChildCount() - 1;; i = 0) {
      return getChildAt(i);
    }
  }
  
  private void layoutForPredictiveAnimations(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, int paramInt1, int paramInt2)
  {
    if ((!paramState.willRunPredictiveAnimations()) || (getChildCount() == 0) || (paramState.isPreLayout()) || (!supportsPredictiveItemAnimations())) {}
    for (;;)
    {
      return;
      int i = 0;
      int j = 0;
      List localList = paramRecycler.getScrapList();
      int k = localList.size();
      int m = getPosition(getChildAt(0));
      int n = 0;
      if (n < k)
      {
        RecyclerView.ViewHolder localViewHolder = (RecyclerView.ViewHolder)localList.get(n);
        if (localViewHolder.isRemoved()) {}
        for (;;)
        {
          n++;
          break;
          int i1;
          if (localViewHolder.getLayoutPosition() < m)
          {
            i1 = 1;
            label112:
            if (i1 == this.mShouldReverseLayout) {
              break label156;
            }
          }
          label156:
          for (int i2 = -1;; i2 = 1)
          {
            if (i2 != -1) {
              break label162;
            }
            i += this.mOrientationHelper.getDecoratedMeasurement(localViewHolder.itemView);
            break;
            i1 = 0;
            break label112;
          }
          label162:
          j += this.mOrientationHelper.getDecoratedMeasurement(localViewHolder.itemView);
        }
      }
      this.mLayoutState.mScrapList = localList;
      if (i > 0)
      {
        updateLayoutStateToFillStart(getPosition(getChildClosestToStart()), paramInt1);
        this.mLayoutState.mExtra = i;
        this.mLayoutState.mAvailable = 0;
        this.mLayoutState.assignPositionFromScrapList();
        fill(paramRecycler, this.mLayoutState, paramState, false);
      }
      if (j > 0)
      {
        updateLayoutStateToFillEnd(getPosition(getChildClosestToEnd()), paramInt2);
        this.mLayoutState.mExtra = j;
        this.mLayoutState.mAvailable = 0;
        this.mLayoutState.assignPositionFromScrapList();
        fill(paramRecycler, this.mLayoutState, paramState, false);
      }
      this.mLayoutState.mScrapList = null;
    }
  }
  
  private void logChildren()
  {
    Log.d("LinearLayoutManager", "internal representation of views on the screen");
    for (int i = 0; i < getChildCount(); i++)
    {
      View localView = getChildAt(i);
      Log.d("LinearLayoutManager", "item " + getPosition(localView) + ", coord:" + this.mOrientationHelper.getDecoratedStart(localView));
    }
    Log.d("LinearLayoutManager", "==============");
  }
  
  private void recycleByLayoutState(RecyclerView.Recycler paramRecycler, LayoutState paramLayoutState)
  {
    if ((!paramLayoutState.mRecycle) || (paramLayoutState.mInfinite)) {}
    for (;;)
    {
      return;
      if (paramLayoutState.mLayoutDirection == -1) {
        recycleViewsFromEnd(paramRecycler, paramLayoutState.mScrollingOffset);
      } else {
        recycleViewsFromStart(paramRecycler, paramLayoutState.mScrollingOffset);
      }
    }
  }
  
  private void recycleViewsFromEnd(RecyclerView.Recycler paramRecycler, int paramInt)
  {
    int i = getChildCount();
    if (paramInt < 0) {}
    label84:
    label145:
    for (;;)
    {
      return;
      int j = this.mOrientationHelper.getEnd() - paramInt;
      View localView;
      if (this.mShouldReverseLayout) {
        for (paramInt = 0;; paramInt++)
        {
          if (paramInt >= i) {
            break label84;
          }
          localView = getChildAt(paramInt);
          if ((this.mOrientationHelper.getDecoratedStart(localView) < j) || (this.mOrientationHelper.getTransformedStartWithDecoration(localView) < j))
          {
            recycleChildren(paramRecycler, 0, paramInt);
            break;
          }
        }
      } else {
        for (paramInt = i - 1;; paramInt--)
        {
          if (paramInt < 0) {
            break label145;
          }
          localView = getChildAt(paramInt);
          if ((this.mOrientationHelper.getDecoratedStart(localView) < j) || (this.mOrientationHelper.getTransformedStartWithDecoration(localView) < j))
          {
            recycleChildren(paramRecycler, i - 1, paramInt);
            break;
          }
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
  
  private boolean updateAnchorFromChildren(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, AnchorInfo paramAnchorInfo)
  {
    boolean bool1 = false;
    boolean bool2;
    if (getChildCount() == 0) {
      bool2 = bool1;
    }
    label85:
    do
    {
      do
      {
        for (;;)
        {
          return bool2;
          View localView = getFocusedChild();
          if ((localView == null) || (!paramAnchorInfo.isViewValidAsAnchor(localView, paramState))) {
            break;
          }
          paramAnchorInfo.assignFromViewAndKeepVisibleRect(localView, getPosition(localView));
          bool2 = true;
        }
        bool2 = bool1;
      } while (this.mLastStackFromEnd != this.mStackFromEnd);
      if (!paramAnchorInfo.mLayoutFromEnd) {
        break;
      }
      paramRecycler = findReferenceChildClosestToEnd(paramRecycler, paramState);
      bool2 = bool1;
    } while (paramRecycler == null);
    paramAnchorInfo.assignFromView(paramRecycler, getPosition(paramRecycler));
    if ((!paramState.isPreLayout()) && (supportsPredictiveItemAnimations()))
    {
      if ((this.mOrientationHelper.getDecoratedStart(paramRecycler) < this.mOrientationHelper.getEndAfterPadding()) && (this.mOrientationHelper.getDecoratedEnd(paramRecycler) >= this.mOrientationHelper.getStartAfterPadding())) {
        break label199;
      }
      i = 1;
      label156:
      if (i != 0) {
        if (!paramAnchorInfo.mLayoutFromEnd) {
          break label205;
        }
      }
    }
    label199:
    label205:
    for (int i = this.mOrientationHelper.getEndAfterPadding();; i = this.mOrientationHelper.getStartAfterPadding())
    {
      paramAnchorInfo.mCoordinate = i;
      bool2 = true;
      break;
      paramRecycler = findReferenceChildClosestToStart(paramRecycler, paramState);
      break label85;
      i = 0;
      break label156;
    }
  }
  
  private boolean updateAnchorFromPendingData(RecyclerView.State paramState, AnchorInfo paramAnchorInfo)
  {
    boolean bool1 = false;
    boolean bool2 = true;
    boolean bool3;
    if ((paramState.isPreLayout()) || (this.mPendingScrollPosition == -1)) {
      bool3 = false;
    }
    for (;;)
    {
      return bool3;
      if ((this.mPendingScrollPosition < 0) || (this.mPendingScrollPosition >= paramState.getItemCount()))
      {
        this.mPendingScrollPosition = -1;
        this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
        bool3 = false;
      }
      else
      {
        paramAnchorInfo.mPosition = this.mPendingScrollPosition;
        if ((this.mPendingSavedState != null) && (this.mPendingSavedState.hasValidAnchor()))
        {
          paramAnchorInfo.mLayoutFromEnd = this.mPendingSavedState.mAnchorLayoutFromEnd;
          if (paramAnchorInfo.mLayoutFromEnd)
          {
            paramAnchorInfo.mCoordinate = (this.mOrientationHelper.getEndAfterPadding() - this.mPendingSavedState.mAnchorOffset);
            bool3 = bool2;
          }
          else
          {
            paramAnchorInfo.mCoordinate = (this.mOrientationHelper.getStartAfterPadding() + this.mPendingSavedState.mAnchorOffset);
            bool3 = bool2;
          }
        }
        else if (this.mPendingScrollPositionOffset == Integer.MIN_VALUE)
        {
          paramState = findViewByPosition(this.mPendingScrollPosition);
          int i;
          if (paramState != null)
          {
            if (this.mOrientationHelper.getDecoratedMeasurement(paramState) > this.mOrientationHelper.getTotalSpace())
            {
              paramAnchorInfo.assignCoordinateFromPadding();
              bool3 = bool2;
            }
            else if (this.mOrientationHelper.getDecoratedStart(paramState) - this.mOrientationHelper.getStartAfterPadding() < 0)
            {
              paramAnchorInfo.mCoordinate = this.mOrientationHelper.getStartAfterPadding();
              paramAnchorInfo.mLayoutFromEnd = false;
              bool3 = bool2;
            }
            else if (this.mOrientationHelper.getEndAfterPadding() - this.mOrientationHelper.getDecoratedEnd(paramState) < 0)
            {
              paramAnchorInfo.mCoordinate = this.mOrientationHelper.getEndAfterPadding();
              paramAnchorInfo.mLayoutFromEnd = true;
              bool3 = bool2;
            }
            else
            {
              if (paramAnchorInfo.mLayoutFromEnd) {}
              for (i = this.mOrientationHelper.getDecoratedEnd(paramState) + this.mOrientationHelper.getTotalSpaceChange();; i = this.mOrientationHelper.getDecoratedStart(paramState))
              {
                paramAnchorInfo.mCoordinate = i;
                bool3 = bool2;
                break;
              }
            }
          }
          else
          {
            if (getChildCount() > 0)
            {
              i = getPosition(getChildAt(0));
              if (this.mPendingScrollPosition >= i) {
                break label399;
              }
            }
            label399:
            for (bool3 = true;; bool3 = false)
            {
              if (bool3 == this.mPendingScrollPositionBottom) {
                bool1 = true;
              }
              paramAnchorInfo.mLayoutFromEnd = bool1;
              paramAnchorInfo.assignCoordinateFromPadding();
              bool3 = bool2;
              break;
            }
          }
        }
        else
        {
          paramAnchorInfo.mLayoutFromEnd = this.mPendingScrollPositionBottom;
          if (this.mPendingScrollPositionBottom)
          {
            paramAnchorInfo.mCoordinate = (this.mOrientationHelper.getEndAfterPadding() - this.mPendingScrollPositionOffset);
            bool3 = bool2;
          }
          else
          {
            paramAnchorInfo.mCoordinate = (this.mOrientationHelper.getStartAfterPadding() + this.mPendingScrollPositionOffset);
            bool3 = bool2;
          }
        }
      }
    }
  }
  
  private void updateAnchorInfoForLayout(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, AnchorInfo paramAnchorInfo)
  {
    if (updateAnchorFromPendingData(paramState, paramAnchorInfo)) {
      return;
    }
    if (!updateAnchorFromChildren(paramRecycler, paramState, paramAnchorInfo))
    {
      paramAnchorInfo.assignCoordinateFromPadding();
      if (!this.mStackFromEnd) {
        break label48;
      }
    }
    label48:
    for (int i = paramState.getItemCount() - 1;; i = 0)
    {
      paramAnchorInfo.mPosition = i;
      break;
      break;
    }
  }
  
  private void updateLayoutState(int paramInt1, int paramInt2, boolean paramBoolean, RecyclerView.State paramState)
  {
    int i = -1;
    int j = 1;
    this.mLayoutState.mInfinite = resolveIsInfinite();
    this.mLayoutState.mExtra = getExtraLayoutSpace(paramState);
    this.mLayoutState.mLayoutDirection = paramInt1;
    if (paramInt1 == 1)
    {
      paramState = this.mLayoutState;
      paramState.mExtra += this.mOrientationHelper.getEndPadding();
      localObject = getChildClosestToEnd();
      paramState = this.mLayoutState;
      if (this.mShouldReverseLayout) {}
      for (paramInt1 = i;; paramInt1 = 1)
      {
        paramState.mItemDirection = paramInt1;
        this.mLayoutState.mCurrentPosition = (getPosition((View)localObject) + this.mLayoutState.mItemDirection);
        this.mLayoutState.mOffset = this.mOrientationHelper.getDecoratedEnd((View)localObject);
        paramInt1 = this.mOrientationHelper.getDecoratedEnd((View)localObject) - this.mOrientationHelper.getEndAfterPadding();
        this.mLayoutState.mAvailable = paramInt2;
        if (paramBoolean)
        {
          paramState = this.mLayoutState;
          paramState.mAvailable -= paramInt1;
        }
        this.mLayoutState.mScrollingOffset = paramInt1;
        return;
      }
    }
    paramState = getChildClosestToStart();
    Object localObject = this.mLayoutState;
    ((LayoutState)localObject).mExtra += this.mOrientationHelper.getStartAfterPadding();
    localObject = this.mLayoutState;
    if (this.mShouldReverseLayout) {}
    for (paramInt1 = j;; paramInt1 = -1)
    {
      ((LayoutState)localObject).mItemDirection = paramInt1;
      this.mLayoutState.mCurrentPosition = (getPosition(paramState) + this.mLayoutState.mItemDirection);
      this.mLayoutState.mOffset = this.mOrientationHelper.getDecoratedStart(paramState);
      paramInt1 = -this.mOrientationHelper.getDecoratedStart(paramState) + this.mOrientationHelper.getStartAfterPadding();
      break;
    }
  }
  
  private void updateLayoutStateToFillEnd(int paramInt1, int paramInt2)
  {
    this.mLayoutState.mAvailable = (this.mOrientationHelper.getEndAfterPadding() - paramInt2);
    LayoutState localLayoutState = this.mLayoutState;
    if (this.mShouldReverseLayout) {}
    for (int i = -1;; i = 1)
    {
      localLayoutState.mItemDirection = i;
      this.mLayoutState.mCurrentPosition = paramInt1;
      this.mLayoutState.mLayoutDirection = 1;
      this.mLayoutState.mOffset = paramInt2;
      this.mLayoutState.mScrollingOffset = Integer.MIN_VALUE;
      return;
    }
  }
  
  private void updateLayoutStateToFillEnd(AnchorInfo paramAnchorInfo)
  {
    updateLayoutStateToFillEnd(paramAnchorInfo.mPosition, paramAnchorInfo.mCoordinate);
  }
  
  private void updateLayoutStateToFillStart(int paramInt1, int paramInt2)
  {
    this.mLayoutState.mAvailable = (paramInt2 - this.mOrientationHelper.getStartAfterPadding());
    this.mLayoutState.mCurrentPosition = paramInt1;
    LayoutState localLayoutState = this.mLayoutState;
    if (this.mShouldReverseLayout) {}
    for (paramInt1 = 1;; paramInt1 = -1)
    {
      localLayoutState.mItemDirection = paramInt1;
      this.mLayoutState.mLayoutDirection = -1;
      this.mLayoutState.mOffset = paramInt2;
      this.mLayoutState.mScrollingOffset = Integer.MIN_VALUE;
      return;
    }
  }
  
  private void updateLayoutStateToFillStart(AnchorInfo paramAnchorInfo)
  {
    updateLayoutStateToFillStart(paramAnchorInfo.mPosition, paramAnchorInfo.mCoordinate);
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
  
  public void collectAdjacentPrefetchPositions(int paramInt1, int paramInt2, RecyclerView.State paramState, RecyclerView.LayoutManager.LayoutPrefetchRegistry paramLayoutPrefetchRegistry)
  {
    if (this.mOrientation == 0) {}
    while ((getChildCount() == 0) || (paramInt1 == 0))
    {
      return;
      paramInt1 = paramInt2;
    }
    ensureLayoutState();
    if (paramInt1 > 0) {}
    for (paramInt2 = 1;; paramInt2 = -1)
    {
      updateLayoutState(paramInt2, Math.abs(paramInt1), true, paramState);
      collectPrefetchPositionsForLayoutState(paramState, this.mLayoutState, paramLayoutPrefetchRegistry);
      break;
    }
  }
  
  public void collectInitialPrefetchPositions(int paramInt, RecyclerView.LayoutManager.LayoutPrefetchRegistry paramLayoutPrefetchRegistry)
  {
    int i = -1;
    boolean bool;
    int j;
    if ((this.mPendingSavedState != null) && (this.mPendingSavedState.hasValidAnchor()))
    {
      bool = this.mPendingSavedState.mAnchorLayoutFromEnd;
      j = this.mPendingSavedState.mAnchorPosition;
      if (!bool) {
        break label140;
      }
    }
    for (;;)
    {
      int k = 0;
      int m = j;
      for (j = k; (j < this.mInitialPrefetchItemCount) && (m >= 0) && (m < paramInt); j++)
      {
        paramLayoutPrefetchRegistry.addPosition(m, 0);
        m += i;
      }
      resolveShouldLayoutReverse();
      bool = this.mShouldReverseLayout;
      if (this.mPendingScrollPosition == -1)
      {
        if (bool) {}
        for (j = paramInt - 1;; j = 0) {
          break;
        }
      }
      j = this.mPendingScrollPosition;
      break;
      label140:
      i = 1;
    }
  }
  
  void collectPrefetchPositionsForLayoutState(RecyclerView.State paramState, LayoutState paramLayoutState, RecyclerView.LayoutManager.LayoutPrefetchRegistry paramLayoutPrefetchRegistry)
  {
    int i = paramLayoutState.mCurrentPosition;
    if ((i >= 0) && (i < paramState.getItemCount())) {
      paramLayoutPrefetchRegistry.addPosition(i, Math.max(0, paramLayoutState.mScrollingOffset));
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
    int i = 0;
    PointF localPointF;
    if (getChildCount() == 0) {
      localPointF = null;
    }
    for (;;)
    {
      return localPointF;
      if (paramInt < getPosition(getChildAt(0))) {
        i = 1;
      }
      if (i != this.mShouldReverseLayout) {}
      for (paramInt = -1;; paramInt = 1)
      {
        if (this.mOrientation != 0) {
          break label64;
        }
        localPointF = new PointF(paramInt, 0.0F);
        break;
      }
      label64:
      localPointF = new PointF(0.0F, paramInt);
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
  
  int convertFocusDirectionToLayoutDirection(int paramInt)
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
  
  LayoutState createLayoutState()
  {
    return new LayoutState();
  }
  
  void ensureLayoutState()
  {
    if (this.mLayoutState == null) {
      this.mLayoutState = createLayoutState();
    }
  }
  
  int fill(RecyclerView.Recycler paramRecycler, LayoutState paramLayoutState, RecyclerView.State paramState, boolean paramBoolean)
  {
    int i = paramLayoutState.mAvailable;
    if (paramLayoutState.mScrollingOffset != Integer.MIN_VALUE)
    {
      if (paramLayoutState.mAvailable < 0) {
        paramLayoutState.mScrollingOffset += paramLayoutState.mAvailable;
      }
      recycleByLayoutState(paramRecycler, paramLayoutState);
    }
    int j = paramLayoutState.mAvailable + paramLayoutState.mExtra;
    LayoutChunkResult localLayoutChunkResult = this.mLayoutChunkResult;
    if (((paramLayoutState.mInfinite) || (j > 0)) && (paramLayoutState.hasMore(paramState)))
    {
      localLayoutChunkResult.resetInternal();
      layoutChunk(paramRecycler, paramState, paramLayoutState, localLayoutChunkResult);
      if (!localLayoutChunkResult.mFinished) {
        break label108;
      }
    }
    for (;;)
    {
      return i - paramLayoutState.mAvailable;
      label108:
      paramLayoutState.mOffset += localLayoutChunkResult.mConsumed * paramLayoutState.mLayoutDirection;
      int k;
      if ((localLayoutChunkResult.mIgnoreConsumed) && (this.mLayoutState.mScrapList == null))
      {
        k = j;
        if (paramState.isPreLayout()) {}
      }
      else
      {
        paramLayoutState.mAvailable -= localLayoutChunkResult.mConsumed;
        k = j - localLayoutChunkResult.mConsumed;
      }
      if (paramLayoutState.mScrollingOffset != Integer.MIN_VALUE)
      {
        paramLayoutState.mScrollingOffset += localLayoutChunkResult.mConsumed;
        if (paramLayoutState.mAvailable < 0) {
          paramLayoutState.mScrollingOffset += paramLayoutState.mAvailable;
        }
        recycleByLayoutState(paramRecycler, paramLayoutState);
      }
      j = k;
      if (!paramBoolean) {
        break;
      }
      j = k;
      if (!localLayoutChunkResult.mFocusable) {
        break;
      }
    }
  }
  
  public int findFirstCompletelyVisibleItemPosition()
  {
    View localView = findOneVisibleChild(0, getChildCount(), true, false);
    if (localView == null) {}
    for (int i = -1;; i = getPosition(localView)) {
      return i;
    }
  }
  
  public int findFirstVisibleItemPosition()
  {
    View localView = findOneVisibleChild(0, getChildCount(), false, true);
    if (localView == null) {}
    for (int i = -1;; i = getPosition(localView)) {
      return i;
    }
  }
  
  public int findLastCompletelyVisibleItemPosition()
  {
    int i = -1;
    View localView = findOneVisibleChild(getChildCount() - 1, -1, true, false);
    if (localView == null) {}
    for (;;)
    {
      return i;
      i = getPosition(localView);
    }
  }
  
  public int findLastVisibleItemPosition()
  {
    int i = -1;
    View localView = findOneVisibleChild(getChildCount() - 1, -1, false, true);
    if (localView == null) {}
    for (;;)
    {
      return i;
      i = getPosition(localView);
    }
  }
  
  View findOnePartiallyOrCompletelyInvisibleChild(int paramInt1, int paramInt2)
  {
    ensureLayoutState();
    int i;
    View localView;
    if (paramInt2 > paramInt1)
    {
      i = 1;
      if (i != 0) {
        break label40;
      }
      localView = getChildAt(paramInt1);
    }
    for (;;)
    {
      return localView;
      if (paramInt2 < paramInt1)
      {
        i = -1;
        break;
      }
      i = 0;
      break;
      label40:
      int j;
      if (this.mOrientationHelper.getDecoratedStart(getChildAt(paramInt1)) < this.mOrientationHelper.getStartAfterPadding()) {
        j = 16644;
      }
      for (i = 16388;; i = 4097)
      {
        if (this.mOrientation != 0) {
          break label107;
        }
        localView = this.mHorizontalBoundCheck.findOneViewWithinBoundFlags(paramInt1, paramInt2, j, i);
        break;
        j = 4161;
      }
      label107:
      localView = this.mVerticalBoundCheck.findOneViewWithinBoundFlags(paramInt1, paramInt2, j, i);
    }
  }
  
  View findOneVisibleChild(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
  {
    ensureLayoutState();
    int i = 0;
    int j;
    if (paramBoolean1)
    {
      j = 24579;
      if (paramBoolean2) {
        i = 320;
      }
      if (this.mOrientation != 0) {
        break label59;
      }
    }
    label59:
    for (View localView = this.mHorizontalBoundCheck.findOneViewWithinBoundFlags(paramInt1, paramInt2, j, i);; localView = this.mVerticalBoundCheck.findOneViewWithinBoundFlags(paramInt1, paramInt2, j, i))
    {
      return localView;
      j = 320;
      break;
    }
  }
  
  View findReferenceChild(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, int paramInt1, int paramInt2, int paramInt3)
  {
    ensureLayoutState();
    paramState = null;
    paramRecycler = null;
    int i = this.mOrientationHelper.getStartAfterPadding();
    int j = this.mOrientationHelper.getEndAfterPadding();
    int k;
    View localView;
    Object localObject1;
    Object localObject2;
    if (paramInt2 > paramInt1)
    {
      k = 1;
      if (paramInt1 == paramInt2) {
        break label183;
      }
      localView = getChildAt(paramInt1);
      int m = getPosition(localView);
      localObject1 = paramState;
      localObject2 = paramRecycler;
      if (m >= 0)
      {
        localObject1 = paramState;
        localObject2 = paramRecycler;
        if (m < paramInt3)
        {
          if (!((RecyclerView.LayoutParams)localView.getLayoutParams()).isItemRemoved()) {
            break label131;
          }
          localObject1 = paramState;
          localObject2 = paramRecycler;
          if (paramState == null)
          {
            localObject2 = paramRecycler;
            localObject1 = localView;
          }
        }
      }
    }
    for (;;)
    {
      paramInt1 += k;
      paramState = (RecyclerView.State)localObject1;
      paramRecycler = (RecyclerView.Recycler)localObject2;
      break;
      k = -1;
      break;
      label131:
      if (this.mOrientationHelper.getDecoratedStart(localView) < j)
      {
        localObject2 = localView;
        if (this.mOrientationHelper.getDecoratedEnd(localView) >= i) {
          break label190;
        }
      }
      localObject1 = paramState;
      localObject2 = paramRecycler;
      if (paramRecycler == null)
      {
        localObject1 = paramState;
        localObject2 = localView;
      }
    }
    label183:
    if (paramRecycler != null) {}
    for (;;)
    {
      localObject2 = paramRecycler;
      label190:
      return (View)localObject2;
      paramRecycler = paramState;
    }
  }
  
  public View findViewByPosition(int paramInt)
  {
    int i = getChildCount();
    Object localObject;
    if (i == 0) {
      localObject = null;
    }
    for (;;)
    {
      return (View)localObject;
      int j = paramInt - getPosition(getChildAt(0));
      if ((j >= 0) && (j < i))
      {
        View localView = getChildAt(j);
        localObject = localView;
        if (getPosition(localView) == paramInt) {}
      }
      else
      {
        localObject = super.findViewByPosition(paramInt);
      }
    }
  }
  
  public RecyclerView.LayoutParams generateDefaultLayoutParams()
  {
    return new RecyclerView.LayoutParams(-2, -2);
  }
  
  protected int getExtraLayoutSpace(RecyclerView.State paramState)
  {
    if (paramState.hasTargetScrollPosition()) {}
    for (int i = this.mOrientationHelper.getTotalSpace();; i = 0) {
      return i;
    }
  }
  
  public int getInitialPrefetchItemCount()
  {
    return this.mInitialPrefetchItemCount;
  }
  
  public int getOrientation()
  {
    return this.mOrientation;
  }
  
  public boolean getRecycleChildrenOnDetach()
  {
    return this.mRecycleChildrenOnDetach;
  }
  
  public boolean getReverseLayout()
  {
    return this.mReverseLayout;
  }
  
  public boolean getStackFromEnd()
  {
    return this.mStackFromEnd;
  }
  
  public boolean isAutoMeasureEnabled()
  {
    return true;
  }
  
  protected boolean isLayoutRTL()
  {
    boolean bool = true;
    if (getLayoutDirection() == 1) {}
    for (;;)
    {
      return bool;
      bool = false;
    }
  }
  
  public boolean isSmoothScrollbarEnabled()
  {
    return this.mSmoothScrollbarEnabled;
  }
  
  void layoutChunk(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, LayoutState paramLayoutState, LayoutChunkResult paramLayoutChunkResult)
  {
    paramState = paramLayoutState.next(paramRecycler);
    if (paramState == null)
    {
      paramLayoutChunkResult.mFinished = true;
      return;
    }
    paramRecycler = (RecyclerView.LayoutParams)paramState.getLayoutParams();
    boolean bool1;
    boolean bool2;
    label49:
    label61:
    int i;
    int j;
    label120:
    int k;
    int m;
    if (paramLayoutState.mScrapList == null)
    {
      bool1 = this.mShouldReverseLayout;
      if (paramLayoutState.mLayoutDirection == -1)
      {
        bool2 = true;
        if (bool1 != bool2) {
          break label197;
        }
        addView(paramState);
        measureChildWithMargins(paramState, 0, 0);
        paramLayoutChunkResult.mConsumed = this.mOrientationHelper.getDecoratedMeasurement(paramState);
        if (this.mOrientation != 1) {
          break label296;
        }
        if (!isLayoutRTL()) {
          break label253;
        }
        i = getWidth() - getPaddingRight();
        j = i - this.mOrientationHelper.getDecoratedMeasurementInOther(paramState);
        if (paramLayoutState.mLayoutDirection != -1) {
          break label275;
        }
        k = paramLayoutState.mOffset;
        m = paramLayoutState.mOffset - paramLayoutChunkResult.mConsumed;
      }
    }
    for (;;)
    {
      layoutDecoratedWithMargins(paramState, j, m, i, k);
      if ((paramRecycler.isItemRemoved()) || (paramRecycler.isItemChanged())) {
        paramLayoutChunkResult.mIgnoreConsumed = true;
      }
      paramLayoutChunkResult.mFocusable = paramState.hasFocusable();
      break;
      bool2 = false;
      break label49;
      label197:
      addView(paramState, 0);
      break label61;
      bool1 = this.mShouldReverseLayout;
      if (paramLayoutState.mLayoutDirection == -1) {}
      for (bool2 = true;; bool2 = false)
      {
        if (bool1 != bool2) {
          break label244;
        }
        addDisappearingView(paramState);
        break;
      }
      label244:
      addDisappearingView(paramState, 0);
      break label61;
      label253:
      j = getPaddingLeft();
      i = j + this.mOrientationHelper.getDecoratedMeasurementInOther(paramState);
      break label120;
      label275:
      m = paramLayoutState.mOffset;
      k = paramLayoutState.mOffset + paramLayoutChunkResult.mConsumed;
      continue;
      label296:
      m = getPaddingTop();
      k = m + this.mOrientationHelper.getDecoratedMeasurementInOther(paramState);
      if (paramLayoutState.mLayoutDirection == -1)
      {
        i = paramLayoutState.mOffset;
        j = paramLayoutState.mOffset - paramLayoutChunkResult.mConsumed;
      }
      else
      {
        j = paramLayoutState.mOffset;
        i = paramLayoutState.mOffset + paramLayoutChunkResult.mConsumed;
      }
    }
  }
  
  void onAnchorReady(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, AnchorInfo paramAnchorInfo, int paramInt) {}
  
  public void onDetachedFromWindow(RecyclerView paramRecyclerView, RecyclerView.Recycler paramRecycler)
  {
    super.onDetachedFromWindow(paramRecyclerView, paramRecycler);
    if (this.mRecycleChildrenOnDetach)
    {
      removeAndRecycleAllViews(paramRecycler);
      paramRecycler.clear();
    }
  }
  
  public View onFocusSearchFailed(View paramView, int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    resolveShouldLayoutReverse();
    if (getChildCount() == 0) {
      paramView = null;
    }
    for (;;)
    {
      return paramView;
      paramInt = convertFocusDirectionToLayoutDirection(paramInt);
      if (paramInt == Integer.MIN_VALUE)
      {
        paramView = null;
      }
      else
      {
        ensureLayoutState();
        ensureLayoutState();
        updateLayoutState(paramInt, (int)(0.33333334F * this.mOrientationHelper.getTotalSpace()), false, paramState);
        this.mLayoutState.mScrollingOffset = Integer.MIN_VALUE;
        this.mLayoutState.mRecycle = false;
        fill(paramRecycler, this.mLayoutState, paramState, true);
        if (paramInt == -1)
        {
          paramRecycler = findPartiallyOrCompletelyInvisibleChildClosestToStart(paramRecycler, paramState);
          label103:
          if (paramInt != -1) {
            break label140;
          }
        }
        label140:
        for (paramView = getChildClosestToStart();; paramView = getChildClosestToEnd())
        {
          if (!paramView.hasFocusable()) {
            break label148;
          }
          if (paramRecycler != null) {
            break;
          }
          paramView = null;
          break;
          paramRecycler = findPartiallyOrCompletelyInvisibleChildClosestToEnd(paramRecycler, paramState);
          break label103;
        }
        label148:
        paramView = paramRecycler;
      }
    }
  }
  
  public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    super.onInitializeAccessibilityEvent(paramAccessibilityEvent);
    if (getChildCount() > 0)
    {
      paramAccessibilityEvent.setFromIndex(findFirstVisibleItemPosition());
      paramAccessibilityEvent.setToIndex(findLastVisibleItemPosition());
    }
  }
  
  public void onLayoutChildren(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    if (((this.mPendingSavedState != null) || (this.mPendingScrollPosition != -1)) && (paramState.getItemCount() == 0))
    {
      removeAndRecycleAllViews(paramRecycler);
      return;
    }
    if ((this.mPendingSavedState != null) && (this.mPendingSavedState.hasValidAnchor())) {
      this.mPendingScrollPosition = this.mPendingSavedState.mAnchorPosition;
    }
    ensureLayoutState();
    this.mLayoutState.mRecycle = false;
    resolveShouldLayoutReverse();
    Object localObject = getFocusedChild();
    label143:
    int i;
    int j;
    label163:
    int k;
    int m;
    label286:
    label302:
    label322:
    int n;
    if ((!this.mAnchorInfo.mValid) || (this.mPendingScrollPosition != -1) || (this.mPendingSavedState != null))
    {
      this.mAnchorInfo.reset();
      this.mAnchorInfo.mLayoutFromEnd = (this.mShouldReverseLayout ^ this.mStackFromEnd);
      updateAnchorInfoForLayout(paramRecycler, paramState, this.mAnchorInfo);
      this.mAnchorInfo.mValid = true;
      i = getExtraLayoutSpace(paramState);
      if (this.mLayoutState.mLastScrollDelta < 0) {
        break label736;
      }
      j = 0;
      k = j + this.mOrientationHelper.getStartAfterPadding();
      m = i + this.mOrientationHelper.getEndPadding();
      i = m;
      j = k;
      if (paramState.isPreLayout())
      {
        i = m;
        j = k;
        if (this.mPendingScrollPosition != -1)
        {
          i = m;
          j = k;
          if (this.mPendingScrollPositionOffset != Integer.MIN_VALUE)
          {
            localObject = findViewByPosition(this.mPendingScrollPosition);
            i = m;
            j = k;
            if (localObject != null)
            {
              if (!this.mPendingScrollPositionBottom) {
                break label746;
              }
              i = this.mOrientationHelper.getEndAfterPadding() - this.mOrientationHelper.getDecoratedEnd((View)localObject) - this.mPendingScrollPositionOffset;
              if (i <= 0) {
                break label780;
              }
              j = k + i;
              i = m;
            }
          }
        }
      }
      if (!this.mAnchorInfo.mLayoutFromEnd) {
        break label800;
      }
      if (!this.mShouldReverseLayout) {
        break label794;
      }
      k = 1;
      onAnchorReady(paramRecycler, paramState, this.mAnchorInfo, k);
      detachAndScrapAttachedViews(paramRecycler);
      this.mLayoutState.mInfinite = resolveIsInfinite();
      this.mLayoutState.mIsPreLayout = paramState.isPreLayout();
      if (!this.mAnchorInfo.mLayoutFromEnd) {
        break label819;
      }
      updateLayoutStateToFillStart(this.mAnchorInfo);
      this.mLayoutState.mExtra = j;
      fill(paramRecycler, this.mLayoutState, paramState, false);
      k = this.mLayoutState.mOffset;
      n = this.mLayoutState.mCurrentPosition;
      j = i;
      if (this.mLayoutState.mAvailable > 0) {
        j = i + this.mLayoutState.mAvailable;
      }
      updateLayoutStateToFillEnd(this.mAnchorInfo);
      this.mLayoutState.mExtra = j;
      localObject = this.mLayoutState;
      ((LayoutState)localObject).mCurrentPosition += this.mLayoutState.mItemDirection;
      fill(paramRecycler, this.mLayoutState, paramState, false);
      m = this.mLayoutState.mOffset;
      i = m;
      j = k;
      if (this.mLayoutState.mAvailable > 0)
      {
        i = this.mLayoutState.mAvailable;
        updateLayoutStateToFillStart(n, k);
        this.mLayoutState.mExtra = i;
        fill(paramRecycler, this.mLayoutState, paramState, false);
        j = this.mLayoutState.mOffset;
        i = m;
      }
      label572:
      m = i;
      k = j;
      if (getChildCount() > 0)
      {
        if (!(this.mShouldReverseLayout ^ this.mStackFromEnd)) {
          break label1023;
        }
        m = fixLayoutEndGap(i, paramRecycler, paramState, true);
        k = j + m;
        j = fixLayoutStartGap(k, paramRecycler, paramState, false);
        k += j;
        m = i + m + j;
      }
      label645:
      layoutForPredictiveAnimations(paramRecycler, paramState, k, m);
      if (paramState.isPreLayout()) {
        break label1072;
      }
      this.mOrientationHelper.onLayoutComplete();
    }
    for (;;)
    {
      this.mLastStackFromEnd = this.mStackFromEnd;
      break;
      if ((localObject == null) || ((this.mOrientationHelper.getDecoratedStart((View)localObject) < this.mOrientationHelper.getEndAfterPadding()) && (this.mOrientationHelper.getDecoratedEnd((View)localObject) > this.mOrientationHelper.getStartAfterPadding()))) {
        break label143;
      }
      this.mAnchorInfo.assignFromViewAndKeepVisibleRect((View)localObject, getPosition((View)localObject));
      break label143;
      label736:
      j = i;
      i = 0;
      break label163;
      label746:
      j = this.mOrientationHelper.getDecoratedStart((View)localObject);
      i = this.mOrientationHelper.getStartAfterPadding();
      i = this.mPendingScrollPositionOffset - (j - i);
      break label286;
      label780:
      i = m - i;
      j = k;
      break label302;
      label794:
      k = -1;
      break label322;
      label800:
      if (this.mShouldReverseLayout) {}
      for (k = -1;; k = 1) {
        break;
      }
      label819:
      updateLayoutStateToFillEnd(this.mAnchorInfo);
      this.mLayoutState.mExtra = i;
      fill(paramRecycler, this.mLayoutState, paramState, false);
      k = this.mLayoutState.mOffset;
      n = this.mLayoutState.mCurrentPosition;
      i = j;
      if (this.mLayoutState.mAvailable > 0) {
        i = j + this.mLayoutState.mAvailable;
      }
      updateLayoutStateToFillStart(this.mAnchorInfo);
      this.mLayoutState.mExtra = i;
      localObject = this.mLayoutState;
      ((LayoutState)localObject).mCurrentPosition += this.mLayoutState.mItemDirection;
      fill(paramRecycler, this.mLayoutState, paramState, false);
      m = this.mLayoutState.mOffset;
      i = k;
      j = m;
      if (this.mLayoutState.mAvailable <= 0) {
        break label572;
      }
      i = this.mLayoutState.mAvailable;
      updateLayoutStateToFillEnd(n, k);
      this.mLayoutState.mExtra = i;
      fill(paramRecycler, this.mLayoutState, paramState, false);
      i = this.mLayoutState.mOffset;
      j = m;
      break label572;
      label1023:
      k = fixLayoutStartGap(j, paramRecycler, paramState, true);
      m = i + k;
      i = fixLayoutEndGap(m, paramRecycler, paramState, false);
      k = j + k + i;
      m += i;
      break label645;
      label1072:
      this.mAnchorInfo.reset();
    }
  }
  
  public void onLayoutCompleted(RecyclerView.State paramState)
  {
    super.onLayoutCompleted(paramState);
    this.mPendingSavedState = null;
    this.mPendingScrollPosition = -1;
    this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
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
    SavedState localSavedState;
    if (this.mPendingSavedState != null) {
      localSavedState = new SavedState(this.mPendingSavedState);
    }
    for (;;)
    {
      return localSavedState;
      localSavedState = new SavedState();
      if (getChildCount() > 0)
      {
        ensureLayoutState();
        boolean bool = this.mLastStackFromEnd ^ this.mShouldReverseLayout;
        localSavedState.mAnchorLayoutFromEnd = bool;
        View localView;
        if (bool)
        {
          localView = getChildClosestToEnd();
          localSavedState.mAnchorOffset = (this.mOrientationHelper.getEndAfterPadding() - this.mOrientationHelper.getDecoratedEnd(localView));
          localSavedState.mAnchorPosition = getPosition(localView);
        }
        else
        {
          localView = getChildClosestToStart();
          localSavedState.mAnchorPosition = getPosition(localView);
          localSavedState.mAnchorOffset = (this.mOrientationHelper.getDecoratedStart(localView) - this.mOrientationHelper.getStartAfterPadding());
        }
      }
      else
      {
        localSavedState.invalidateAnchor();
      }
    }
  }
  
  public void prepareForDrop(View paramView1, View paramView2, int paramInt1, int paramInt2)
  {
    assertNotInLayoutOrScroll("Cannot drop a view during a scroll or layout calculation");
    ensureLayoutState();
    resolveShouldLayoutReverse();
    paramInt1 = getPosition(paramView1);
    paramInt2 = getPosition(paramView2);
    if (paramInt1 < paramInt2)
    {
      paramInt1 = 1;
      if (!this.mShouldReverseLayout) {
        break label110;
      }
      if (paramInt1 != 1) {
        break label85;
      }
      scrollToPositionWithOffset(paramInt2, this.mOrientationHelper.getEndAfterPadding() - (this.mOrientationHelper.getDecoratedStart(paramView2) + this.mOrientationHelper.getDecoratedMeasurement(paramView1)));
    }
    for (;;)
    {
      return;
      paramInt1 = -1;
      break;
      label85:
      scrollToPositionWithOffset(paramInt2, this.mOrientationHelper.getEndAfterPadding() - this.mOrientationHelper.getDecoratedEnd(paramView2));
      continue;
      label110:
      if (paramInt1 == -1) {
        scrollToPositionWithOffset(paramInt2, this.mOrientationHelper.getDecoratedStart(paramView2));
      } else {
        scrollToPositionWithOffset(paramInt2, this.mOrientationHelper.getDecoratedEnd(paramView2) - this.mOrientationHelper.getDecoratedMeasurement(paramView1));
      }
    }
  }
  
  protected void recycleChildren(RecyclerView.Recycler paramRecycler, int paramInt1, int paramInt2)
  {
    if (paramInt1 == paramInt2) {}
    for (;;)
    {
      return;
      if (paramInt2 > paramInt1)
      {
        paramInt2--;
        while (paramInt2 >= paramInt1)
        {
          removeAndRecycleViewAt(paramInt2, paramRecycler);
          paramInt2--;
        }
      }
      else
      {
        while (paramInt1 > paramInt2)
        {
          removeAndRecycleViewAt(paramInt1, paramRecycler);
          paramInt1--;
        }
      }
    }
  }
  
  protected void recycleViewsFromStart(RecyclerView.Recycler paramRecycler, int paramInt)
  {
    if (paramInt < 0) {}
    label78:
    label138:
    for (;;)
    {
      return;
      int i = getChildCount();
      int j;
      View localView;
      if (this.mShouldReverseLayout) {
        for (j = i - 1;; j--)
        {
          if (j < 0) {
            break label78;
          }
          localView = getChildAt(j);
          if ((this.mOrientationHelper.getDecoratedEnd(localView) > paramInt) || (this.mOrientationHelper.getTransformedEndWithDecoration(localView) > paramInt))
          {
            recycleChildren(paramRecycler, i - 1, j);
            break;
          }
        }
      } else {
        for (j = 0;; j++)
        {
          if (j >= i) {
            break label138;
          }
          localView = getChildAt(j);
          if ((this.mOrientationHelper.getDecoratedEnd(localView) > paramInt) || (this.mOrientationHelper.getTransformedEndWithDecoration(localView) > paramInt))
          {
            recycleChildren(paramRecycler, 0, j);
            break;
          }
        }
      }
    }
  }
  
  boolean resolveIsInfinite()
  {
    if ((this.mOrientationHelper.getMode() == 0) && (this.mOrientationHelper.getEnd() == 0)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  int scrollBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    int i = 0;
    int j = i;
    if (getChildCount() != 0)
    {
      if (paramInt != 0) {
        break label25;
      }
      j = i;
    }
    label25:
    int k;
    label44:
    int m;
    int n;
    do
    {
      return j;
      this.mLayoutState.mRecycle = true;
      ensureLayoutState();
      if (paramInt <= 0) {
        break;
      }
      k = 1;
      m = Math.abs(paramInt);
      updateLayoutState(k, m, true, paramState);
      n = this.mLayoutState.mScrollingOffset + fill(paramRecycler, this.mLayoutState, paramState, false);
      j = i;
    } while (n < 0);
    if (m > n) {
      paramInt = k * n;
    }
    for (;;)
    {
      this.mOrientationHelper.offsetChildren(-paramInt);
      this.mLayoutState.mLastScrollDelta = paramInt;
      j = paramInt;
      break;
      k = -1;
      break label44;
    }
  }
  
  public int scrollHorizontallyBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    if (this.mOrientation == 1) {}
    for (paramInt = 0;; paramInt = scrollBy(paramInt, paramRecycler, paramState)) {
      return paramInt;
    }
  }
  
  public void scrollToPosition(int paramInt)
  {
    this.mPendingScrollPosition = paramInt;
    this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
    if (this.mPendingSavedState != null) {
      this.mPendingSavedState.invalidateAnchor();
    }
    requestLayout();
  }
  
  public void scrollToPositionWithOffset(int paramInt1, int paramInt2)
  {
    scrollToPositionWithOffset(paramInt1, paramInt2, this.mShouldReverseLayout);
  }
  
  public void scrollToPositionWithOffset(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    this.mPendingScrollPosition = paramInt1;
    this.mPendingScrollPositionOffset = paramInt2;
    this.mPendingScrollPositionBottom = paramBoolean;
    if (this.mPendingSavedState != null) {
      this.mPendingSavedState.invalidateAnchor();
    }
    requestLayout();
  }
  
  public int scrollVerticallyBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    if (this.mOrientation == 0) {}
    for (paramInt = 0;; paramInt = scrollBy(paramInt, paramRecycler, paramState)) {
      return paramInt;
    }
  }
  
  public void setInitialPrefetchItemCount(int paramInt)
  {
    this.mInitialPrefetchItemCount = paramInt;
  }
  
  public void setOrientation(int paramInt)
  {
    if ((paramInt != 0) && (paramInt != 1)) {
      throw new IllegalArgumentException("invalid orientation:" + paramInt);
    }
    assertNotInLayoutOrScroll(null);
    if ((paramInt != this.mOrientation) || (this.mOrientationHelper == null))
    {
      this.mOrientationHelper = OrientationHelper.createOrientationHelper(this, paramInt);
      this.mAnchorInfo.mOrientationHelper = this.mOrientationHelper;
      this.mOrientation = paramInt;
      requestLayout();
    }
  }
  
  public void setRecycleChildrenOnDetach(boolean paramBoolean)
  {
    this.mRecycleChildrenOnDetach = paramBoolean;
  }
  
  public void setReverseLayout(boolean paramBoolean)
  {
    assertNotInLayoutOrScroll(null);
    if (paramBoolean == this.mReverseLayout) {}
    for (;;)
    {
      return;
      this.mReverseLayout = paramBoolean;
      requestLayout();
    }
  }
  
  public void setSmoothScrollbarEnabled(boolean paramBoolean)
  {
    this.mSmoothScrollbarEnabled = paramBoolean;
  }
  
  public void setStackFromEnd(boolean paramBoolean)
  {
    assertNotInLayoutOrScroll(null);
    if (this.mStackFromEnd == paramBoolean) {}
    for (;;)
    {
      return;
      this.mStackFromEnd = paramBoolean;
      requestLayout();
    }
  }
  
  boolean shouldMeasureTwice()
  {
    if ((getHeightMode() != NUM) && (getWidthMode() != NUM) && (hasFlexibleChildInBothOrientations())) {}
    for (boolean bool = true;; bool = false) {
      return bool;
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
    if ((this.mPendingSavedState == null) && (this.mLastStackFromEnd == this.mStackFromEnd)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  void validateChildOrder()
  {
    boolean bool1 = true;
    boolean bool2 = true;
    Log.d("LinearLayoutManager", "validating child count " + getChildCount());
    if (getChildCount() < 1) {}
    for (;;)
    {
      return;
      int i = getPosition(getChildAt(0));
      int j = this.mOrientationHelper.getDecoratedStart(getChildAt(0));
      int k;
      Object localObject;
      int m;
      int n;
      if (this.mShouldReverseLayout) {
        for (k = 1; k < getChildCount(); k++)
        {
          localObject = getChildAt(k);
          m = getPosition((View)localObject);
          n = this.mOrientationHelper.getDecoratedStart((View)localObject);
          if (m < i)
          {
            logChildren();
            localObject = new StringBuilder().append("detected invalid position. loc invalid? ");
            if (n < j) {}
            for (;;)
            {
              throw new RuntimeException(bool2);
              bool2 = false;
            }
          }
          if (n > j)
          {
            logChildren();
            throw new RuntimeException("detected invalid location");
          }
        }
      } else {
        for (k = 1; k < getChildCount(); k++)
        {
          localObject = getChildAt(k);
          m = getPosition((View)localObject);
          n = this.mOrientationHelper.getDecoratedStart((View)localObject);
          if (m < i)
          {
            logChildren();
            localObject = new StringBuilder().append("detected invalid position. loc invalid? ");
            if (n < j) {}
            for (bool2 = bool1;; bool2 = false) {
              throw new RuntimeException(bool2);
            }
          }
          if (n < j)
          {
            logChildren();
            throw new RuntimeException("detected invalid location");
          }
        }
      }
    }
  }
  
  static class AnchorInfo
  {
    int mCoordinate;
    boolean mLayoutFromEnd;
    OrientationHelper mOrientationHelper;
    int mPosition;
    boolean mValid;
    
    AnchorInfo()
    {
      reset();
    }
    
    void assignCoordinateFromPadding()
    {
      if (this.mLayoutFromEnd) {}
      for (int i = this.mOrientationHelper.getEndAfterPadding();; i = this.mOrientationHelper.getStartAfterPadding())
      {
        this.mCoordinate = i;
        return;
      }
    }
    
    public void assignFromView(View paramView, int paramInt)
    {
      if (this.mLayoutFromEnd) {}
      for (this.mCoordinate = (this.mOrientationHelper.getDecoratedEnd(paramView) + this.mOrientationHelper.getTotalSpaceChange());; this.mCoordinate = this.mOrientationHelper.getDecoratedStart(paramView))
      {
        this.mPosition = paramInt;
        return;
      }
    }
    
    public void assignFromViewAndKeepVisibleRect(View paramView, int paramInt)
    {
      int i = this.mOrientationHelper.getTotalSpaceChange();
      if (i >= 0) {
        assignFromView(paramView, paramInt);
      }
      for (;;)
      {
        return;
        this.mPosition = paramInt;
        int j;
        int k;
        if (this.mLayoutFromEnd)
        {
          paramInt = this.mOrientationHelper.getEndAfterPadding() - i - this.mOrientationHelper.getDecoratedEnd(paramView);
          this.mCoordinate = (this.mOrientationHelper.getEndAfterPadding() - paramInt);
          if (paramInt > 0)
          {
            j = this.mOrientationHelper.getDecoratedMeasurement(paramView);
            i = this.mCoordinate;
            k = this.mOrientationHelper.getStartAfterPadding();
            i = i - j - (k + Math.min(this.mOrientationHelper.getDecoratedStart(paramView) - k, 0));
            if (i < 0) {
              this.mCoordinate += Math.min(paramInt, -i);
            }
          }
        }
        else
        {
          int m = this.mOrientationHelper.getDecoratedStart(paramView);
          paramInt = m - this.mOrientationHelper.getStartAfterPadding();
          this.mCoordinate = m;
          if (paramInt > 0)
          {
            j = this.mOrientationHelper.getDecoratedMeasurement(paramView);
            k = this.mOrientationHelper.getEndAfterPadding();
            int n = this.mOrientationHelper.getDecoratedEnd(paramView);
            i = this.mOrientationHelper.getEndAfterPadding() - Math.min(0, k - i - n) - (m + j);
            if (i < 0) {
              this.mCoordinate -= Math.min(paramInt, -i);
            }
          }
        }
      }
    }
    
    boolean isViewValidAsAnchor(View paramView, RecyclerView.State paramState)
    {
      paramView = (RecyclerView.LayoutParams)paramView.getLayoutParams();
      if ((!paramView.isItemRemoved()) && (paramView.getViewLayoutPosition() >= 0) && (paramView.getViewLayoutPosition() < paramState.getItemCount())) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    void reset()
    {
      this.mPosition = -1;
      this.mCoordinate = Integer.MIN_VALUE;
      this.mLayoutFromEnd = false;
      this.mValid = false;
    }
    
    public String toString()
    {
      return "AnchorInfo{mPosition=" + this.mPosition + ", mCoordinate=" + this.mCoordinate + ", mLayoutFromEnd=" + this.mLayoutFromEnd + ", mValid=" + this.mValid + '}';
    }
  }
  
  protected static class LayoutChunkResult
  {
    public int mConsumed;
    public boolean mFinished;
    public boolean mFocusable;
    public boolean mIgnoreConsumed;
    
    void resetInternal()
    {
      this.mConsumed = 0;
      this.mFinished = false;
      this.mIgnoreConsumed = false;
      this.mFocusable = false;
    }
  }
  
  static class LayoutState
  {
    static final int INVALID_LAYOUT = Integer.MIN_VALUE;
    static final int ITEM_DIRECTION_HEAD = -1;
    static final int ITEM_DIRECTION_TAIL = 1;
    static final int LAYOUT_END = 1;
    static final int LAYOUT_START = -1;
    static final int SCROLLING_OFFSET_NaN = Integer.MIN_VALUE;
    static final String TAG = "LLM#LayoutState";
    int mAvailable;
    int mCurrentPosition;
    int mExtra = 0;
    boolean mInfinite;
    boolean mIsPreLayout = false;
    int mItemDirection;
    int mLastScrollDelta;
    int mLayoutDirection;
    int mOffset;
    boolean mRecycle = true;
    List<RecyclerView.ViewHolder> mScrapList = null;
    int mScrollingOffset;
    
    private View nextViewFromScrapList()
    {
      int i = this.mScrapList.size();
      int j = 0;
      View localView;
      if (j < i)
      {
        localView = ((RecyclerView.ViewHolder)this.mScrapList.get(j)).itemView;
        RecyclerView.LayoutParams localLayoutParams = (RecyclerView.LayoutParams)localView.getLayoutParams();
        if (localLayoutParams.isItemRemoved()) {}
        while (this.mCurrentPosition != localLayoutParams.getViewLayoutPosition())
        {
          j++;
          break;
        }
        assignPositionFromScrapList(localView);
      }
      for (;;)
      {
        return localView;
        localView = null;
      }
    }
    
    public void assignPositionFromScrapList()
    {
      assignPositionFromScrapList(null);
    }
    
    public void assignPositionFromScrapList(View paramView)
    {
      paramView = nextViewInLimitedList(paramView);
      if (paramView == null) {}
      for (this.mCurrentPosition = -1;; this.mCurrentPosition = ((RecyclerView.LayoutParams)paramView.getLayoutParams()).getViewLayoutPosition()) {
        return;
      }
    }
    
    boolean hasMore(RecyclerView.State paramState)
    {
      if ((this.mCurrentPosition >= 0) && (this.mCurrentPosition < paramState.getItemCount())) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    void log()
    {
      Log.d("LLM#LayoutState", "avail:" + this.mAvailable + ", ind:" + this.mCurrentPosition + ", dir:" + this.mItemDirection + ", offset:" + this.mOffset + ", layoutDir:" + this.mLayoutDirection);
    }
    
    View next(RecyclerView.Recycler paramRecycler)
    {
      if (this.mScrapList != null) {
        paramRecycler = nextViewFromScrapList();
      }
      for (;;)
      {
        return paramRecycler;
        paramRecycler = paramRecycler.getViewForPosition(this.mCurrentPosition);
        this.mCurrentPosition += this.mItemDirection;
      }
    }
    
    public View nextViewInLimitedList(View paramView)
    {
      int i = this.mScrapList.size();
      Object localObject1 = null;
      int j = Integer.MAX_VALUE;
      int k = 0;
      Object localObject2 = localObject1;
      if (k < i)
      {
        View localView = ((RecyclerView.ViewHolder)this.mScrapList.get(k)).itemView;
        RecyclerView.LayoutParams localLayoutParams = (RecyclerView.LayoutParams)localView.getLayoutParams();
        localObject2 = localObject1;
        int m = j;
        if (localView != paramView)
        {
          if (!localLayoutParams.isItemRemoved()) {
            break label98;
          }
          m = j;
          localObject2 = localObject1;
        }
        label98:
        int n;
        do
        {
          do
          {
            do
            {
              k++;
              localObject1 = localObject2;
              j = m;
              break;
              n = (localLayoutParams.getViewLayoutPosition() - this.mCurrentPosition) * this.mItemDirection;
              localObject2 = localObject1;
              m = j;
            } while (n < 0);
            localObject2 = localObject1;
            m = j;
          } while (n >= j);
          localObject1 = localView;
          m = n;
          localObject2 = localObject1;
        } while (n != 0);
        localObject2 = localObject1;
      }
      return (View)localObject2;
    }
  }
  
  public static class SavedState
    implements Parcelable
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public LinearLayoutManager.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new LinearLayoutManager.SavedState(paramAnonymousParcel);
      }
      
      public LinearLayoutManager.SavedState[] newArray(int paramAnonymousInt)
      {
        return new LinearLayoutManager.SavedState[paramAnonymousInt];
      }
    };
    boolean mAnchorLayoutFromEnd;
    int mAnchorOffset;
    int mAnchorPosition;
    
    public SavedState() {}
    
    SavedState(Parcel paramParcel)
    {
      this.mAnchorPosition = paramParcel.readInt();
      this.mAnchorOffset = paramParcel.readInt();
      if (paramParcel.readInt() == 1) {}
      for (;;)
      {
        this.mAnchorLayoutFromEnd = bool;
        return;
        bool = false;
      }
    }
    
    public SavedState(SavedState paramSavedState)
    {
      this.mAnchorPosition = paramSavedState.mAnchorPosition;
      this.mAnchorOffset = paramSavedState.mAnchorOffset;
      this.mAnchorLayoutFromEnd = paramSavedState.mAnchorLayoutFromEnd;
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    boolean hasValidAnchor()
    {
      if (this.mAnchorPosition >= 0) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    void invalidateAnchor()
    {
      this.mAnchorPosition = -1;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(this.mAnchorPosition);
      paramParcel.writeInt(this.mAnchorOffset);
      if (this.mAnchorLayoutFromEnd) {}
      for (paramInt = 1;; paramInt = 0)
      {
        paramParcel.writeInt(paramInt);
        return;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/support/widget/LinearLayoutManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */