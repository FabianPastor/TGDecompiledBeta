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
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager.LayoutPrefetchRegistry;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.Recycler;
import org.telegram.messenger.support.widget.RecyclerView.SmoothScroller.ScrollVectorProvider;
import org.telegram.messenger.support.widget.RecyclerView.State;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.ViewDropHandler;
import org.telegram.tgnet.ConnectionsManager;

public class LinearLayoutManager extends LayoutManager implements ScrollVectorProvider, ViewDropHandler {
    static final boolean DEBUG = false;
    public static final int HORIZONTAL = 0;
    public static final int INVALID_OFFSET = Integer.MIN_VALUE;
    private static final float MAX_SCROLL_FACTOR = 0.33333334f;
    private static final String TAG = "LinearLayoutManager";
    public static final int VERTICAL = 1;
    final AnchorInfo mAnchorInfo;
    private int mInitialPrefetchItemCount;
    private boolean mLastStackFromEnd;
    private final LayoutChunkResult mLayoutChunkResult;
    private LayoutState mLayoutState;
    int mOrientation;
    OrientationHelper mOrientationHelper;
    SavedState mPendingSavedState;
    int mPendingScrollPosition;
    boolean mPendingScrollPositionBottom;
    int mPendingScrollPositionOffset;
    private boolean mRecycleChildrenOnDetach;
    private boolean mReverseLayout;
    boolean mShouldReverseLayout;
    private boolean mSmoothScrollbarEnabled;
    private boolean mStackFromEnd;

    static class AnchorInfo {
        int mCoordinate;
        boolean mLayoutFromEnd;
        OrientationHelper mOrientationHelper;
        int mPosition;
        boolean mValid;

        AnchorInfo() {
            reset();
        }

        void reset() {
            this.mPosition = -1;
            this.mCoordinate = Integer.MIN_VALUE;
            this.mLayoutFromEnd = false;
            this.mValid = false;
        }

        void assignCoordinateFromPadding() {
            int endAfterPadding;
            if (this.mLayoutFromEnd) {
                endAfterPadding = this.mOrientationHelper.getEndAfterPadding();
            } else {
                endAfterPadding = this.mOrientationHelper.getStartAfterPadding();
            }
            this.mCoordinate = endAfterPadding;
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("AnchorInfo{mPosition=");
            stringBuilder.append(this.mPosition);
            stringBuilder.append(", mCoordinate=");
            stringBuilder.append(this.mCoordinate);
            stringBuilder.append(", mLayoutFromEnd=");
            stringBuilder.append(this.mLayoutFromEnd);
            stringBuilder.append(", mValid=");
            stringBuilder.append(this.mValid);
            stringBuilder.append('}');
            return stringBuilder.toString();
        }

        boolean isViewValidAsAnchor(View child, State state) {
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            return !lp.isItemRemoved() && lp.getViewLayoutPosition() >= 0 && lp.getViewLayoutPosition() < state.getItemCount();
        }

        public void assignFromViewAndKeepVisibleRect(View child, int position) {
            int spaceChange = this.mOrientationHelper.getTotalSpaceChange();
            if (spaceChange >= 0) {
                assignFromView(child, position);
                return;
            }
            this.mPosition = position;
            if (this.mLayoutFromEnd) {
                int previousEndMargin = (this.mOrientationHelper.getEndAfterPadding() - spaceChange) - this.mOrientationHelper.getDecoratedEnd(child);
                this.mCoordinate = this.mOrientationHelper.getEndAfterPadding() - previousEndMargin;
                if (previousEndMargin > 0) {
                    int estimatedChildStart = this.mCoordinate - this.mOrientationHelper.getDecoratedMeasurement(child);
                    int layoutStart = this.mOrientationHelper.getStartAfterPadding();
                    int startMargin = estimatedChildStart - (Math.min(this.mOrientationHelper.getDecoratedStart(child) - layoutStart, 0) + layoutStart);
                    if (startMargin < 0) {
                        this.mCoordinate += Math.min(previousEndMargin, -startMargin);
                    }
                }
            } else {
                int childStart = this.mOrientationHelper.getDecoratedStart(child);
                int startMargin2 = childStart - this.mOrientationHelper.getStartAfterPadding();
                this.mCoordinate = childStart;
                if (startMargin2 > 0) {
                    int endMargin = (this.mOrientationHelper.getEndAfterPadding() - Math.min(0, (this.mOrientationHelper.getEndAfterPadding() - spaceChange) - this.mOrientationHelper.getDecoratedEnd(child))) - (this.mOrientationHelper.getDecoratedMeasurement(child) + childStart);
                    if (endMargin < 0) {
                        this.mCoordinate -= Math.min(startMargin2, -endMargin);
                    }
                }
            }
        }

        public void assignFromView(View child, int position) {
            if (this.mLayoutFromEnd) {
                this.mCoordinate = this.mOrientationHelper.getDecoratedEnd(child) + this.mOrientationHelper.getTotalSpaceChange();
            } else {
                this.mCoordinate = this.mOrientationHelper.getDecoratedStart(child);
            }
            this.mPosition = position;
        }
    }

    protected static class LayoutChunkResult {
        public int mConsumed;
        public boolean mFinished;
        public boolean mFocusable;
        public boolean mIgnoreConsumed;

        protected LayoutChunkResult() {
        }

        void resetInternal() {
            this.mConsumed = 0;
            this.mFinished = false;
            this.mIgnoreConsumed = false;
            this.mFocusable = false;
        }
    }

    static class LayoutState {
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
        List<ViewHolder> mScrapList = null;
        int mScrollingOffset;

        LayoutState() {
        }

        boolean hasMore(State state) {
            return this.mCurrentPosition >= 0 && this.mCurrentPosition < state.getItemCount();
        }

        View next(Recycler recycler) {
            if (this.mScrapList != null) {
                return nextViewFromScrapList();
            }
            View view = recycler.getViewForPosition(this.mCurrentPosition);
            this.mCurrentPosition += this.mItemDirection;
            return view;
        }

        private View nextViewFromScrapList() {
            int size = this.mScrapList.size();
            for (int i = 0; i < size; i++) {
                View view = ((ViewHolder) this.mScrapList.get(i)).itemView;
                LayoutParams lp = (LayoutParams) view.getLayoutParams();
                if (!lp.isItemRemoved()) {
                    if (this.mCurrentPosition == lp.getViewLayoutPosition()) {
                        assignPositionFromScrapList(view);
                        return view;
                    }
                }
            }
            return null;
        }

        public void assignPositionFromScrapList() {
            assignPositionFromScrapList(null);
        }

        public void assignPositionFromScrapList(View ignore) {
            View closest = nextViewInLimitedList(ignore);
            if (closest == null) {
                this.mCurrentPosition = -1;
            } else {
                this.mCurrentPosition = ((LayoutParams) closest.getLayoutParams()).getViewLayoutPosition();
            }
        }

        public View nextViewInLimitedList(View ignore) {
            int size = this.mScrapList.size();
            View closest = null;
            int closestDistance = ConnectionsManager.DEFAULT_DATACENTER_ID;
            for (int i = 0; i < size; i++) {
                View view = ((ViewHolder) this.mScrapList.get(i)).itemView;
                LayoutParams lp = (LayoutParams) view.getLayoutParams();
                if (view != ignore) {
                    if (!lp.isItemRemoved()) {
                        int distance = (lp.getViewLayoutPosition() - this.mCurrentPosition) * this.mItemDirection;
                        if (distance >= 0) {
                            if (distance < closestDistance) {
                                closest = view;
                                closestDistance = distance;
                                if (distance == 0) {
                                    break;
                                }
                            } else {
                                continue;
                            }
                        }
                    }
                }
            }
            return closest;
        }

        void log() {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("avail:");
            stringBuilder.append(this.mAvailable);
            stringBuilder.append(", ind:");
            stringBuilder.append(this.mCurrentPosition);
            stringBuilder.append(", dir:");
            stringBuilder.append(this.mItemDirection);
            stringBuilder.append(", offset:");
            stringBuilder.append(this.mOffset);
            stringBuilder.append(", layoutDir:");
            stringBuilder.append(this.mLayoutDirection);
            Log.d(str, stringBuilder.toString());
        }
    }

    public static class SavedState implements Parcelable {
        public static final Creator<SavedState> CREATOR = new C06551();
        boolean mAnchorLayoutFromEnd;
        int mAnchorOffset;
        int mAnchorPosition;

        /* renamed from: org.telegram.messenger.support.widget.LinearLayoutManager$SavedState$1 */
        static class C06551 implements Creator<SavedState> {
            C06551() {
            }

            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        }

        SavedState(Parcel in) {
            this.mAnchorPosition = in.readInt();
            this.mAnchorOffset = in.readInt();
            boolean z = true;
            if (in.readInt() != 1) {
                z = false;
            }
            this.mAnchorLayoutFromEnd = z;
        }

        public SavedState(SavedState other) {
            this.mAnchorPosition = other.mAnchorPosition;
            this.mAnchorOffset = other.mAnchorOffset;
            this.mAnchorLayoutFromEnd = other.mAnchorLayoutFromEnd;
        }

        boolean hasValidAnchor() {
            return this.mAnchorPosition >= 0;
        }

        void invalidateAnchor() {
            this.mAnchorPosition = -1;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.mAnchorPosition);
            dest.writeInt(this.mAnchorOffset);
            dest.writeInt(this.mAnchorLayoutFromEnd);
        }
    }

    public LinearLayoutManager(Context context) {
        this(context, 1, false);
    }

    public LinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        this.mOrientation = 1;
        this.mReverseLayout = false;
        this.mShouldReverseLayout = false;
        this.mStackFromEnd = false;
        this.mSmoothScrollbarEnabled = true;
        this.mPendingScrollPosition = -1;
        this.mPendingScrollPositionBottom = true;
        this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
        this.mPendingSavedState = null;
        this.mAnchorInfo = new AnchorInfo();
        this.mLayoutChunkResult = new LayoutChunkResult();
        this.mInitialPrefetchItemCount = 2;
        setOrientation(orientation);
        setReverseLayout(reverseLayout);
    }

    public boolean isAutoMeasureEnabled() {
        return true;
    }

    public LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }

    public boolean getRecycleChildrenOnDetach() {
        return this.mRecycleChildrenOnDetach;
    }

    public void setRecycleChildrenOnDetach(boolean recycleChildrenOnDetach) {
        this.mRecycleChildrenOnDetach = recycleChildrenOnDetach;
    }

    public void onDetachedFromWindow(RecyclerView view, Recycler recycler) {
        super.onDetachedFromWindow(view, recycler);
        if (this.mRecycleChildrenOnDetach) {
            removeAndRecycleAllViews(recycler);
            recycler.clear();
        }
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        if (getChildCount() > 0) {
            event.setFromIndex(findFirstVisibleItemPosition());
            event.setToIndex(findLastVisibleItemPosition());
        }
    }

    public Parcelable onSaveInstanceState() {
        if (this.mPendingSavedState != null) {
            return new SavedState(this.mPendingSavedState);
        }
        SavedState state = new SavedState();
        if (getChildCount() > 0) {
            ensureLayoutState();
            boolean didLayoutFromEnd = this.mLastStackFromEnd ^ this.mShouldReverseLayout;
            state.mAnchorLayoutFromEnd = didLayoutFromEnd;
            View refChild;
            if (didLayoutFromEnd) {
                refChild = getChildClosestToEnd();
                state.mAnchorOffset = this.mOrientationHelper.getEndAfterPadding() - this.mOrientationHelper.getDecoratedEnd(refChild);
                state.mAnchorPosition = getPosition(refChild);
            } else {
                refChild = getChildClosestToStart();
                state.mAnchorPosition = getPosition(refChild);
                state.mAnchorOffset = this.mOrientationHelper.getDecoratedStart(refChild) - this.mOrientationHelper.getStartAfterPadding();
            }
        } else {
            state.invalidateAnchor();
        }
        return state;
    }

    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof SavedState) {
            this.mPendingSavedState = (SavedState) state;
            requestLayout();
        }
    }

    public boolean canScrollHorizontally() {
        return this.mOrientation == 0;
    }

    public boolean canScrollVertically() {
        return this.mOrientation == 1;
    }

    public void setStackFromEnd(boolean stackFromEnd) {
        assertNotInLayoutOrScroll(null);
        if (this.mStackFromEnd != stackFromEnd) {
            this.mStackFromEnd = stackFromEnd;
            requestLayout();
        }
    }

    public boolean getStackFromEnd() {
        return this.mStackFromEnd;
    }

    public int getOrientation() {
        return this.mOrientation;
    }

    public void setOrientation(int orientation) {
        if (orientation == 0 || orientation == 1) {
            assertNotInLayoutOrScroll(null);
            if (orientation != this.mOrientation || this.mOrientationHelper == null) {
                this.mOrientationHelper = OrientationHelper.createOrientationHelper(this, orientation);
                this.mAnchorInfo.mOrientationHelper = this.mOrientationHelper;
                this.mOrientation = orientation;
                requestLayout();
                return;
            }
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("invalid orientation:");
        stringBuilder.append(orientation);
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    private void resolveShouldLayoutReverse() {
        if (this.mOrientation != 1) {
            if (isLayoutRTL()) {
                this.mShouldReverseLayout = this.mReverseLayout ^ true;
                return;
            }
        }
        this.mShouldReverseLayout = this.mReverseLayout;
    }

    public boolean getReverseLayout() {
        return this.mReverseLayout;
    }

    public void setReverseLayout(boolean reverseLayout) {
        assertNotInLayoutOrScroll(null);
        if (reverseLayout != this.mReverseLayout) {
            this.mReverseLayout = reverseLayout;
            requestLayout();
        }
    }

    public View findViewByPosition(int position) {
        int childCount = getChildCount();
        if (childCount == 0) {
            return null;
        }
        int viewPosition = position - getPosition(getChildAt(0));
        if (viewPosition >= 0 && viewPosition < childCount) {
            View child = getChildAt(viewPosition);
            if (getPosition(child) == position) {
                return child;
            }
        }
        return super.findViewByPosition(position);
    }

    protected int getExtraLayoutSpace(State state) {
        if (state.hasTargetScrollPosition()) {
            return this.mOrientationHelper.getTotalSpace();
        }
        return 0;
    }

    public void smoothScrollToPosition(RecyclerView recyclerView, State state, int position) {
        LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext());
        linearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);
    }

    public PointF computeScrollVectorForPosition(int targetPosition) {
        if (getChildCount() == 0) {
            return null;
        }
        boolean z = false;
        int i = 1;
        if (targetPosition < getPosition(getChildAt(0))) {
            z = true;
        }
        if (z != this.mShouldReverseLayout) {
            i = -1;
        }
        int direction = i;
        if (this.mOrientation == 0) {
            return new PointF((float) direction, 0.0f);
        }
        return new PointF(0.0f, (float) direction);
    }

    public void onLayoutChildren(Recycler recycler, State state) {
        int firstLayoutDirection = -1;
        if (!(this.mPendingSavedState == null && this.mPendingScrollPosition == -1) && state.getItemCount() == 0) {
            removeAndRecycleAllViews(recycler);
            return;
        }
        int extra;
        int extraForEnd;
        int extraForStart;
        View existing;
        int upcomingOffset;
        int lastElement;
        int startOffset;
        int fixOffset;
        int fixOffset2;
        if (this.mPendingSavedState != null && this.mPendingSavedState.hasValidAnchor()) {
            this.mPendingScrollPosition = this.mPendingSavedState.mAnchorPosition;
        }
        ensureLayoutState();
        this.mLayoutState.mRecycle = false;
        resolveShouldLayoutReverse();
        View focused = getFocusedChild();
        if (this.mAnchorInfo.mValid && this.mPendingScrollPosition == -1) {
            if (this.mPendingSavedState == null) {
                if (focused != null && (this.mOrientationHelper.getDecoratedStart(focused) >= this.mOrientationHelper.getEndAfterPadding() || this.mOrientationHelper.getDecoratedEnd(focused) <= this.mOrientationHelper.getStartAfterPadding())) {
                    this.mAnchorInfo.assignFromViewAndKeepVisibleRect(focused, getPosition(focused));
                }
                extra = getExtraLayoutSpace(state);
                if (this.mLayoutState.mLastScrollDelta < 0) {
                    extraForEnd = extra;
                    extraForStart = 0;
                } else {
                    extraForStart = extra;
                    extraForEnd = 0;
                }
                extraForStart += this.mOrientationHelper.getStartAfterPadding();
                extraForEnd += this.mOrientationHelper.getEndPadding();
                if (!(!state.isPreLayout() || this.mPendingScrollPosition == -1 || this.mPendingScrollPositionOffset == Integer.MIN_VALUE)) {
                    existing = findViewByPosition(this.mPendingScrollPosition);
                    if (existing != null) {
                        if (this.mPendingScrollPositionBottom) {
                            upcomingOffset = this.mPendingScrollPositionOffset - (this.mOrientationHelper.getDecoratedStart(existing) - this.mOrientationHelper.getStartAfterPadding());
                        } else {
                            upcomingOffset = (this.mOrientationHelper.getEndAfterPadding() - this.mOrientationHelper.getDecoratedEnd(existing)) - this.mPendingScrollPositionOffset;
                        }
                        if (upcomingOffset <= 0) {
                            extraForStart += upcomingOffset;
                        } else {
                            extraForEnd -= upcomingOffset;
                        }
                    }
                }
                if (this.mAnchorInfo.mLayoutFromEnd) {
                    if (this.mShouldReverseLayout) {
                        firstLayoutDirection = 1;
                    }
                } else if (this.mShouldReverseLayout) {
                    firstLayoutDirection = 1;
                }
                onAnchorReady(recycler, state, this.mAnchorInfo, firstLayoutDirection);
                detachAndScrapAttachedViews(recycler);
                this.mLayoutState.mInfinite = resolveIsInfinite();
                this.mLayoutState.mIsPreLayout = state.isPreLayout();
                if (this.mAnchorInfo.mLayoutFromEnd) {
                    updateLayoutStateToFillEnd(this.mAnchorInfo);
                    this.mLayoutState.mExtra = extraForEnd;
                    fill(recycler, this.mLayoutState, state, false);
                    upcomingOffset = this.mLayoutState.mOffset;
                    lastElement = this.mLayoutState.mCurrentPosition;
                    if (this.mLayoutState.mAvailable > 0) {
                        extraForStart += this.mLayoutState.mAvailable;
                    }
                    updateLayoutStateToFillStart(this.mAnchorInfo);
                    this.mLayoutState.mExtra = extraForStart;
                    LayoutState layoutState = this.mLayoutState;
                    layoutState.mCurrentPosition += this.mLayoutState.mItemDirection;
                    fill(recycler, this.mLayoutState, state, false);
                    startOffset = this.mLayoutState.mOffset;
                    if (this.mLayoutState.mAvailable > 0) {
                        extraForEnd = this.mLayoutState.mAvailable;
                        updateLayoutStateToFillEnd(lastElement, upcomingOffset);
                        this.mLayoutState.mExtra = extraForEnd;
                        fill(recycler, this.mLayoutState, state, false);
                        upcomingOffset = this.mLayoutState.mOffset;
                    }
                } else {
                    updateLayoutStateToFillStart(this.mAnchorInfo);
                    this.mLayoutState.mExtra = extraForStart;
                    fill(recycler, this.mLayoutState, state, false);
                    lastElement = this.mLayoutState.mOffset;
                    startOffset = this.mLayoutState.mCurrentPosition;
                    if (this.mLayoutState.mAvailable > 0) {
                        extraForEnd += this.mLayoutState.mAvailable;
                    }
                    updateLayoutStateToFillEnd(this.mAnchorInfo);
                    this.mLayoutState.mExtra = extraForEnd;
                    LayoutState layoutState2 = this.mLayoutState;
                    layoutState2.mCurrentPosition += this.mLayoutState.mItemDirection;
                    fill(recycler, this.mLayoutState, state, false);
                    upcomingOffset = this.mLayoutState.mOffset;
                    if (this.mLayoutState.mAvailable > 0) {
                        extraForStart = this.mLayoutState.mAvailable;
                        updateLayoutStateToFillStart(startOffset, lastElement);
                        this.mLayoutState.mExtra = extraForStart;
                        fill(recycler, this.mLayoutState, state, false);
                        lastElement = this.mLayoutState.mOffset;
                    }
                    startOffset = lastElement;
                }
                if (getChildCount() > 0) {
                    if ((this.mShouldReverseLayout ^ this.mStackFromEnd) == 0) {
                        fixOffset = fixLayoutEndGap(upcomingOffset, recycler, state, true);
                        startOffset += fixOffset;
                        upcomingOffset += fixOffset;
                        fixOffset2 = fixLayoutStartGap(startOffset, recycler, state, false);
                        startOffset += fixOffset2;
                        upcomingOffset += fixOffset2;
                    } else {
                        fixOffset = fixLayoutStartGap(startOffset, recycler, state, true);
                        startOffset += fixOffset;
                        upcomingOffset += fixOffset;
                        fixOffset2 = fixLayoutEndGap(upcomingOffset, recycler, state, false);
                        startOffset += fixOffset2;
                        upcomingOffset += fixOffset2;
                    }
                }
                layoutForPredictiveAnimations(recycler, state, startOffset, upcomingOffset);
                if (state.isPreLayout()) {
                    this.mOrientationHelper.onLayoutComplete();
                } else {
                    this.mAnchorInfo.reset();
                }
                this.mLastStackFromEnd = this.mStackFromEnd;
            }
        }
        this.mAnchorInfo.reset();
        this.mAnchorInfo.mLayoutFromEnd = this.mShouldReverseLayout ^ this.mStackFromEnd;
        updateAnchorInfoForLayout(recycler, state, this.mAnchorInfo);
        this.mAnchorInfo.mValid = true;
        extra = getExtraLayoutSpace(state);
        if (this.mLayoutState.mLastScrollDelta < 0) {
            extraForStart = extra;
            extraForEnd = 0;
        } else {
            extraForEnd = extra;
            extraForStart = 0;
        }
        extraForStart += this.mOrientationHelper.getStartAfterPadding();
        extraForEnd += this.mOrientationHelper.getEndPadding();
        existing = findViewByPosition(this.mPendingScrollPosition);
        if (existing != null) {
            if (this.mPendingScrollPositionBottom) {
                upcomingOffset = this.mPendingScrollPositionOffset - (this.mOrientationHelper.getDecoratedStart(existing) - this.mOrientationHelper.getStartAfterPadding());
            } else {
                upcomingOffset = (this.mOrientationHelper.getEndAfterPadding() - this.mOrientationHelper.getDecoratedEnd(existing)) - this.mPendingScrollPositionOffset;
            }
            if (upcomingOffset <= 0) {
                extraForEnd -= upcomingOffset;
            } else {
                extraForStart += upcomingOffset;
            }
        }
        if (this.mAnchorInfo.mLayoutFromEnd) {
            if (this.mShouldReverseLayout) {
                firstLayoutDirection = 1;
            }
        } else if (this.mShouldReverseLayout) {
            firstLayoutDirection = 1;
        }
        onAnchorReady(recycler, state, this.mAnchorInfo, firstLayoutDirection);
        detachAndScrapAttachedViews(recycler);
        this.mLayoutState.mInfinite = resolveIsInfinite();
        this.mLayoutState.mIsPreLayout = state.isPreLayout();
        if (this.mAnchorInfo.mLayoutFromEnd) {
            updateLayoutStateToFillEnd(this.mAnchorInfo);
            this.mLayoutState.mExtra = extraForEnd;
            fill(recycler, this.mLayoutState, state, false);
            upcomingOffset = this.mLayoutState.mOffset;
            lastElement = this.mLayoutState.mCurrentPosition;
            if (this.mLayoutState.mAvailable > 0) {
                extraForStart += this.mLayoutState.mAvailable;
            }
            updateLayoutStateToFillStart(this.mAnchorInfo);
            this.mLayoutState.mExtra = extraForStart;
            LayoutState layoutState3 = this.mLayoutState;
            layoutState3.mCurrentPosition += this.mLayoutState.mItemDirection;
            fill(recycler, this.mLayoutState, state, false);
            startOffset = this.mLayoutState.mOffset;
            if (this.mLayoutState.mAvailable > 0) {
                extraForEnd = this.mLayoutState.mAvailable;
                updateLayoutStateToFillEnd(lastElement, upcomingOffset);
                this.mLayoutState.mExtra = extraForEnd;
                fill(recycler, this.mLayoutState, state, false);
                upcomingOffset = this.mLayoutState.mOffset;
            }
        } else {
            updateLayoutStateToFillStart(this.mAnchorInfo);
            this.mLayoutState.mExtra = extraForStart;
            fill(recycler, this.mLayoutState, state, false);
            lastElement = this.mLayoutState.mOffset;
            startOffset = this.mLayoutState.mCurrentPosition;
            if (this.mLayoutState.mAvailable > 0) {
                extraForEnd += this.mLayoutState.mAvailable;
            }
            updateLayoutStateToFillEnd(this.mAnchorInfo);
            this.mLayoutState.mExtra = extraForEnd;
            LayoutState layoutState22 = this.mLayoutState;
            layoutState22.mCurrentPosition += this.mLayoutState.mItemDirection;
            fill(recycler, this.mLayoutState, state, false);
            upcomingOffset = this.mLayoutState.mOffset;
            if (this.mLayoutState.mAvailable > 0) {
                extraForStart = this.mLayoutState.mAvailable;
                updateLayoutStateToFillStart(startOffset, lastElement);
                this.mLayoutState.mExtra = extraForStart;
                fill(recycler, this.mLayoutState, state, false);
                lastElement = this.mLayoutState.mOffset;
            }
            startOffset = lastElement;
        }
        if (getChildCount() > 0) {
            if ((this.mShouldReverseLayout ^ this.mStackFromEnd) == 0) {
                fixOffset = fixLayoutStartGap(startOffset, recycler, state, true);
                startOffset += fixOffset;
                upcomingOffset += fixOffset;
                fixOffset2 = fixLayoutEndGap(upcomingOffset, recycler, state, false);
                startOffset += fixOffset2;
                upcomingOffset += fixOffset2;
            } else {
                fixOffset = fixLayoutEndGap(upcomingOffset, recycler, state, true);
                startOffset += fixOffset;
                upcomingOffset += fixOffset;
                fixOffset2 = fixLayoutStartGap(startOffset, recycler, state, false);
                startOffset += fixOffset2;
                upcomingOffset += fixOffset2;
            }
        }
        layoutForPredictiveAnimations(recycler, state, startOffset, upcomingOffset);
        if (state.isPreLayout()) {
            this.mAnchorInfo.reset();
        } else {
            this.mOrientationHelper.onLayoutComplete();
        }
        this.mLastStackFromEnd = this.mStackFromEnd;
    }

    public void onLayoutCompleted(State state) {
        super.onLayoutCompleted(state);
        this.mPendingSavedState = null;
        this.mPendingScrollPosition = -1;
        this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
        this.mAnchorInfo.reset();
    }

    void onAnchorReady(Recycler recycler, State state, AnchorInfo anchorInfo, int firstLayoutItemDirection) {
    }

    private void layoutForPredictiveAnimations(Recycler recycler, State state, int startOffset, int endOffset) {
        int direction;
        int i;
        LinearLayoutManager linearLayoutManager = this;
        Recycler recycler2 = recycler;
        State state2 = state;
        if (!(!state.willRunPredictiveAnimations() || getChildCount() == 0 || state.isPreLayout())) {
            if (supportsPredictiveItemAnimations()) {
                List<ViewHolder> scrapList = recycler.getScrapList();
                int scrapSize = scrapList.size();
                int firstChildPos = getPosition(getChildAt(0));
                int scrapExtraEnd = 0;
                int scrapExtraStart = 0;
                for (int i2 = 0; i2 < scrapSize; i2++) {
                    ViewHolder scrap = (ViewHolder) scrapList.get(i2);
                    if (!scrap.isRemoved()) {
                        direction = 1;
                        if ((scrap.getLayoutPosition() < firstChildPos) != linearLayoutManager.mShouldReverseLayout) {
                            direction = -1;
                        }
                        if (direction == -1) {
                            scrapExtraStart += linearLayoutManager.mOrientationHelper.getDecoratedMeasurement(scrap.itemView);
                        } else {
                            scrapExtraEnd += linearLayoutManager.mOrientationHelper.getDecoratedMeasurement(scrap.itemView);
                        }
                    }
                }
                linearLayoutManager.mLayoutState.mScrapList = scrapList;
                if (scrapExtraStart > 0) {
                    updateLayoutStateToFillStart(getPosition(getChildClosestToStart()), startOffset);
                    linearLayoutManager.mLayoutState.mExtra = scrapExtraStart;
                    linearLayoutManager.mLayoutState.mAvailable = 0;
                    linearLayoutManager.mLayoutState.assignPositionFromScrapList();
                    fill(recycler2, linearLayoutManager.mLayoutState, state2, false);
                } else {
                    i = startOffset;
                }
                if (scrapExtraEnd > 0) {
                    updateLayoutStateToFillEnd(getPosition(getChildClosestToEnd()), endOffset);
                    linearLayoutManager.mLayoutState.mExtra = scrapExtraEnd;
                    linearLayoutManager.mLayoutState.mAvailable = 0;
                    linearLayoutManager.mLayoutState.assignPositionFromScrapList();
                    fill(recycler2, linearLayoutManager.mLayoutState, state2, false);
                } else {
                    direction = endOffset;
                }
                linearLayoutManager.mLayoutState.mScrapList = null;
                return;
            }
        }
        i = startOffset;
        direction = endOffset;
    }

    private void updateAnchorInfoForLayout(Recycler recycler, State state, AnchorInfo anchorInfo) {
        if (!updateAnchorFromPendingData(state, anchorInfo) && !updateAnchorFromChildren(recycler, state, anchorInfo)) {
            anchorInfo.assignCoordinateFromPadding();
            anchorInfo.mPosition = this.mStackFromEnd ? state.getItemCount() - 1 : 0;
        }
    }

    private boolean updateAnchorFromChildren(Recycler recycler, State state, AnchorInfo anchorInfo) {
        boolean notVisible = false;
        if (getChildCount() == 0) {
            return false;
        }
        View focused = getFocusedChild();
        if (focused != null && anchorInfo.isViewValidAsAnchor(focused, state)) {
            anchorInfo.assignFromViewAndKeepVisibleRect(focused, getPosition(focused));
            return true;
        } else if (this.mLastStackFromEnd != this.mStackFromEnd) {
            return false;
        } else {
            View referenceChild;
            if (anchorInfo.mLayoutFromEnd) {
                referenceChild = findReferenceChildClosestToEnd(recycler, state);
            } else {
                referenceChild = findReferenceChildClosestToStart(recycler, state);
            }
            if (referenceChild == null) {
                return false;
            }
            anchorInfo.assignFromView(referenceChild, getPosition(referenceChild));
            if (!state.isPreLayout() && supportsPredictiveItemAnimations()) {
                int startAfterPadding;
                if (this.mOrientationHelper.getDecoratedStart(referenceChild) < this.mOrientationHelper.getEndAfterPadding()) {
                    if (this.mOrientationHelper.getDecoratedEnd(referenceChild) >= this.mOrientationHelper.getStartAfterPadding()) {
                        if (notVisible) {
                            if (anchorInfo.mLayoutFromEnd) {
                                startAfterPadding = this.mOrientationHelper.getStartAfterPadding();
                            } else {
                                startAfterPadding = this.mOrientationHelper.getEndAfterPadding();
                            }
                            anchorInfo.mCoordinate = startAfterPadding;
                        }
                    }
                }
                notVisible = true;
                if (notVisible) {
                    if (anchorInfo.mLayoutFromEnd) {
                        startAfterPadding = this.mOrientationHelper.getStartAfterPadding();
                    } else {
                        startAfterPadding = this.mOrientationHelper.getEndAfterPadding();
                    }
                    anchorInfo.mCoordinate = startAfterPadding;
                }
            }
            return true;
        }
    }

    private boolean updateAnchorFromPendingData(State state, AnchorInfo anchorInfo) {
        boolean z = false;
        if (!state.isPreLayout()) {
            if (this.mPendingScrollPosition != -1) {
                if (this.mPendingScrollPosition >= 0) {
                    if (this.mPendingScrollPosition < state.getItemCount()) {
                        anchorInfo.mPosition = this.mPendingScrollPosition;
                        if (this.mPendingSavedState != null && this.mPendingSavedState.hasValidAnchor()) {
                            anchorInfo.mLayoutFromEnd = this.mPendingSavedState.mAnchorLayoutFromEnd;
                            if (anchorInfo.mLayoutFromEnd) {
                                anchorInfo.mCoordinate = this.mOrientationHelper.getEndAfterPadding() - this.mPendingSavedState.mAnchorOffset;
                            } else {
                                anchorInfo.mCoordinate = this.mOrientationHelper.getStartAfterPadding() + this.mPendingSavedState.mAnchorOffset;
                            }
                            return true;
                        } else if (this.mPendingScrollPositionOffset == Integer.MIN_VALUE) {
                            View child = findViewByPosition(this.mPendingScrollPosition);
                            if (child == null) {
                                if (getChildCount() > 0) {
                                    if ((this.mPendingScrollPosition < getPosition(getChildAt(0))) == this.mPendingScrollPositionBottom) {
                                        z = true;
                                    }
                                    anchorInfo.mLayoutFromEnd = z;
                                }
                                anchorInfo.assignCoordinateFromPadding();
                            } else if (this.mOrientationHelper.getDecoratedMeasurement(child) > this.mOrientationHelper.getTotalSpace()) {
                                anchorInfo.assignCoordinateFromPadding();
                                return true;
                            } else if (this.mOrientationHelper.getDecoratedStart(child) - this.mOrientationHelper.getStartAfterPadding() < 0) {
                                anchorInfo.mCoordinate = this.mOrientationHelper.getStartAfterPadding();
                                anchorInfo.mLayoutFromEnd = false;
                                return true;
                            } else if (this.mOrientationHelper.getEndAfterPadding() - this.mOrientationHelper.getDecoratedEnd(child) < 0) {
                                anchorInfo.mCoordinate = this.mOrientationHelper.getEndAfterPadding();
                                anchorInfo.mLayoutFromEnd = true;
                                return true;
                            } else {
                                int decoratedEnd;
                                if (anchorInfo.mLayoutFromEnd) {
                                    decoratedEnd = this.mOrientationHelper.getDecoratedEnd(child) + this.mOrientationHelper.getTotalSpaceChange();
                                } else {
                                    decoratedEnd = this.mOrientationHelper.getDecoratedStart(child);
                                }
                                anchorInfo.mCoordinate = decoratedEnd;
                            }
                            return true;
                        } else {
                            anchorInfo.mLayoutFromEnd = this.mPendingScrollPositionBottom;
                            if (this.mPendingScrollPositionBottom) {
                                anchorInfo.mCoordinate = this.mOrientationHelper.getEndAfterPadding() - this.mPendingScrollPositionOffset;
                            } else {
                                anchorInfo.mCoordinate = this.mOrientationHelper.getStartAfterPadding() + this.mPendingScrollPositionOffset;
                            }
                            return true;
                        }
                    }
                }
                this.mPendingScrollPosition = -1;
                this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
                return false;
            }
        }
        return false;
    }

    private int fixLayoutEndGap(int endOffset, Recycler recycler, State state, boolean canOffsetChildren) {
        int gap = this.mOrientationHelper.getEndAfterPadding() - endOffset;
        if (gap <= 0) {
            return 0;
        }
        int fixOffset = -scrollBy(-gap, recycler, state);
        endOffset += fixOffset;
        if (canOffsetChildren) {
            gap = this.mOrientationHelper.getEndAfterPadding() - endOffset;
            if (gap > 0) {
                this.mOrientationHelper.offsetChildren(gap);
                return gap + fixOffset;
            }
        }
        return fixOffset;
    }

    private int fixLayoutStartGap(int startOffset, Recycler recycler, State state, boolean canOffsetChildren) {
        int gap = startOffset - this.mOrientationHelper.getStartAfterPadding();
        if (gap <= 0) {
            return 0;
        }
        int fixOffset = -scrollBy(gap, recycler, state);
        startOffset += fixOffset;
        if (canOffsetChildren) {
            gap = startOffset - this.mOrientationHelper.getStartAfterPadding();
            if (gap > 0) {
                this.mOrientationHelper.offsetChildren(-gap);
                return fixOffset - gap;
            }
        }
        return fixOffset;
    }

    private void updateLayoutStateToFillEnd(AnchorInfo anchorInfo) {
        updateLayoutStateToFillEnd(anchorInfo.mPosition, anchorInfo.mCoordinate);
    }

    private void updateLayoutStateToFillEnd(int itemPosition, int offset) {
        this.mLayoutState.mAvailable = this.mOrientationHelper.getEndAfterPadding() - offset;
        this.mLayoutState.mItemDirection = this.mShouldReverseLayout ? -1 : 1;
        this.mLayoutState.mCurrentPosition = itemPosition;
        this.mLayoutState.mLayoutDirection = 1;
        this.mLayoutState.mOffset = offset;
        this.mLayoutState.mScrollingOffset = Integer.MIN_VALUE;
    }

    private void updateLayoutStateToFillStart(AnchorInfo anchorInfo) {
        updateLayoutStateToFillStart(anchorInfo.mPosition, anchorInfo.mCoordinate);
    }

    private void updateLayoutStateToFillStart(int itemPosition, int offset) {
        this.mLayoutState.mAvailable = offset - this.mOrientationHelper.getStartAfterPadding();
        this.mLayoutState.mCurrentPosition = itemPosition;
        this.mLayoutState.mItemDirection = this.mShouldReverseLayout ? 1 : -1;
        this.mLayoutState.mLayoutDirection = -1;
        this.mLayoutState.mOffset = offset;
        this.mLayoutState.mScrollingOffset = Integer.MIN_VALUE;
    }

    protected boolean isLayoutRTL() {
        return getLayoutDirection() == 1;
    }

    void ensureLayoutState() {
        if (this.mLayoutState == null) {
            this.mLayoutState = createLayoutState();
        }
    }

    LayoutState createLayoutState() {
        return new LayoutState();
    }

    public void scrollToPosition(int position) {
        this.mPendingScrollPosition = position;
        this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
        if (this.mPendingSavedState != null) {
            this.mPendingSavedState.invalidateAnchor();
        }
        requestLayout();
    }

    public void scrollToPositionWithOffset(int position, int offset) {
        scrollToPositionWithOffset(position, offset, this.mShouldReverseLayout);
    }

    public void scrollToPositionWithOffset(int position, int offset, boolean bottom) {
        this.mPendingScrollPosition = position;
        this.mPendingScrollPositionOffset = offset;
        this.mPendingScrollPositionBottom = bottom;
        if (this.mPendingSavedState != null) {
            this.mPendingSavedState.invalidateAnchor();
        }
        requestLayout();
    }

    public int scrollHorizontallyBy(int dx, Recycler recycler, State state) {
        if (this.mOrientation == 1) {
            return 0;
        }
        return scrollBy(dx, recycler, state);
    }

    public int scrollVerticallyBy(int dy, Recycler recycler, State state) {
        if (this.mOrientation == 0) {
            return 0;
        }
        return scrollBy(dy, recycler, state);
    }

    public int computeHorizontalScrollOffset(State state) {
        return computeScrollOffset(state);
    }

    public int computeVerticalScrollOffset(State state) {
        return computeScrollOffset(state);
    }

    public int computeHorizontalScrollExtent(State state) {
        return computeScrollExtent(state);
    }

    public int computeVerticalScrollExtent(State state) {
        return computeScrollExtent(state);
    }

    public int computeHorizontalScrollRange(State state) {
        return computeScrollRange(state);
    }

    public int computeVerticalScrollRange(State state) {
        return computeScrollRange(state);
    }

    private int computeScrollOffset(State state) {
        if (getChildCount() == 0) {
            return 0;
        }
        ensureLayoutState();
        return ScrollbarHelper.computeScrollOffset(state, this.mOrientationHelper, findFirstVisibleChildClosestToStart(this.mSmoothScrollbarEnabled ^ true, true), findFirstVisibleChildClosestToEnd(this.mSmoothScrollbarEnabled ^ true, true), this, this.mSmoothScrollbarEnabled, this.mShouldReverseLayout);
    }

    private int computeScrollExtent(State state) {
        if (getChildCount() == 0) {
            return 0;
        }
        ensureLayoutState();
        return ScrollbarHelper.computeScrollExtent(state, this.mOrientationHelper, findFirstVisibleChildClosestToStart(this.mSmoothScrollbarEnabled ^ true, true), findFirstVisibleChildClosestToEnd(this.mSmoothScrollbarEnabled ^ true, true), this, this.mSmoothScrollbarEnabled);
    }

    private int computeScrollRange(State state) {
        if (getChildCount() == 0) {
            return 0;
        }
        ensureLayoutState();
        return ScrollbarHelper.computeScrollRange(state, this.mOrientationHelper, findFirstVisibleChildClosestToStart(this.mSmoothScrollbarEnabled ^ true, true), findFirstVisibleChildClosestToEnd(this.mSmoothScrollbarEnabled ^ true, true), this, this.mSmoothScrollbarEnabled);
    }

    public void setSmoothScrollbarEnabled(boolean enabled) {
        this.mSmoothScrollbarEnabled = enabled;
    }

    public boolean isSmoothScrollbarEnabled() {
        return this.mSmoothScrollbarEnabled;
    }

    private void updateLayoutState(int layoutDirection, int requiredSpace, boolean canUseExistingSpace, State state) {
        this.mLayoutState.mInfinite = resolveIsInfinite();
        this.mLayoutState.mExtra = getExtraLayoutSpace(state);
        this.mLayoutState.mLayoutDirection = layoutDirection;
        int i = -1;
        View child;
        LayoutState layoutState;
        if (layoutDirection == 1) {
            LayoutState layoutState2 = this.mLayoutState;
            layoutState2.mExtra += this.mOrientationHelper.getEndPadding();
            child = getChildClosestToEnd();
            layoutState = this.mLayoutState;
            if (!this.mShouldReverseLayout) {
                i = 1;
            }
            layoutState.mItemDirection = i;
            this.mLayoutState.mCurrentPosition = getPosition(child) + this.mLayoutState.mItemDirection;
            this.mLayoutState.mOffset = this.mOrientationHelper.getDecoratedEnd(child);
            i = this.mOrientationHelper.getDecoratedEnd(child) - this.mOrientationHelper.getEndAfterPadding();
        } else {
            child = getChildClosestToStart();
            layoutState = this.mLayoutState;
            layoutState.mExtra += this.mOrientationHelper.getStartAfterPadding();
            layoutState = this.mLayoutState;
            if (this.mShouldReverseLayout) {
                i = 1;
            }
            layoutState.mItemDirection = i;
            this.mLayoutState.mCurrentPosition = getPosition(child) + this.mLayoutState.mItemDirection;
            this.mLayoutState.mOffset = this.mOrientationHelper.getDecoratedStart(child);
            i = (-this.mOrientationHelper.getDecoratedStart(child)) + this.mOrientationHelper.getStartAfterPadding();
        }
        this.mLayoutState.mAvailable = requiredSpace;
        if (canUseExistingSpace) {
            LayoutState layoutState3 = this.mLayoutState;
            layoutState3.mAvailable -= i;
        }
        this.mLayoutState.mScrollingOffset = i;
    }

    boolean resolveIsInfinite() {
        return this.mOrientationHelper.getMode() == 0 && this.mOrientationHelper.getEnd() == 0;
    }

    void collectPrefetchPositionsForLayoutState(State state, LayoutState layoutState, LayoutPrefetchRegistry layoutPrefetchRegistry) {
        int pos = layoutState.mCurrentPosition;
        if (pos >= 0 && pos < state.getItemCount()) {
            layoutPrefetchRegistry.addPosition(pos, Math.max(0, layoutState.mScrollingOffset));
        }
    }

    public void collectInitialPrefetchPositions(int adapterItemCount, LayoutPrefetchRegistry layoutPrefetchRegistry) {
        int anchorPos;
        int targetPos;
        int direction = -1;
        boolean fromEnd;
        int i;
        if (this.mPendingSavedState == null || !this.mPendingSavedState.hasValidAnchor()) {
            resolveShouldLayoutReverse();
            fromEnd = this.mShouldReverseLayout;
            if (this.mPendingScrollPosition == -1) {
                anchorPos = fromEnd ? adapterItemCount - 1 : 0;
            } else {
                anchorPos = this.mPendingScrollPosition;
                if (fromEnd) {
                    direction = 1;
                }
                targetPos = anchorPos;
                for (i = 0; i < this.mInitialPrefetchItemCount && targetPos >= 0 && targetPos < adapterItemCount; i++) {
                    layoutPrefetchRegistry.addPosition(targetPos, 0);
                    targetPos += direction;
                }
                return;
            }
        }
        fromEnd = this.mPendingSavedState.mAnchorLayoutFromEnd;
        anchorPos = this.mPendingSavedState.mAnchorPosition;
        if (fromEnd) {
            direction = 1;
        }
        targetPos = anchorPos;
        for (i = 0; i < this.mInitialPrefetchItemCount; i++) {
            layoutPrefetchRegistry.addPosition(targetPos, 0);
            targetPos += direction;
        }
    }

    public void setInitialPrefetchItemCount(int itemCount) {
        this.mInitialPrefetchItemCount = itemCount;
    }

    public int getInitialPrefetchItemCount() {
        return this.mInitialPrefetchItemCount;
    }

    public void collectAdjacentPrefetchPositions(int dx, int dy, State state, LayoutPrefetchRegistry layoutPrefetchRegistry) {
        int delta = this.mOrientation == 0 ? dx : dy;
        if (getChildCount() != 0) {
            if (delta != 0) {
                ensureLayoutState();
                updateLayoutState(delta > 0 ? 1 : -1, Math.abs(delta), true, state);
                collectPrefetchPositionsForLayoutState(state, this.mLayoutState, layoutPrefetchRegistry);
            }
        }
    }

    int scrollBy(int dy, Recycler recycler, State state) {
        if (getChildCount() != 0) {
            if (dy != 0) {
                this.mLayoutState.mRecycle = true;
                ensureLayoutState();
                int layoutDirection = dy > 0 ? 1 : -1;
                int absDy = Math.abs(dy);
                updateLayoutState(layoutDirection, absDy, true, state);
                int consumed = this.mLayoutState.mScrollingOffset + fill(recycler, this.mLayoutState, state, false);
                if (consumed < 0) {
                    return 0;
                }
                int scrolled = absDy > consumed ? layoutDirection * consumed : dy;
                this.mOrientationHelper.offsetChildren(-scrolled);
                this.mLayoutState.mLastScrollDelta = scrolled;
                return scrolled;
            }
        }
        return 0;
    }

    public void assertNotInLayoutOrScroll(String message) {
        if (this.mPendingSavedState == null) {
            super.assertNotInLayoutOrScroll(message);
        }
    }

    protected void recycleChildren(Recycler recycler, int startIndex, int endIndex) {
        if (startIndex != endIndex) {
            int i;
            if (endIndex > startIndex) {
                for (i = endIndex - 1; i >= startIndex; i--) {
                    removeAndRecycleViewAt(i, recycler);
                }
            } else {
                for (i = startIndex; i > endIndex; i--) {
                    removeAndRecycleViewAt(i, recycler);
                }
            }
        }
    }

    protected void recycleViewsFromStart(Recycler recycler, int dt) {
        if (dt >= 0) {
            int limit = dt;
            int childCount = getChildCount();
            if (this.mShouldReverseLayout) {
                int i = childCount - 1;
                while (i >= 0) {
                    View child = getChildAt(i);
                    if (this.mOrientationHelper.getDecoratedEnd(child) <= limit) {
                        if (this.mOrientationHelper.getTransformedEndWithDecoration(child) <= limit) {
                            i--;
                        }
                    }
                    recycleChildren(recycler, childCount - 1, i);
                    return;
                }
            }
            int i2 = 0;
            while (i2 < childCount) {
                View child2 = getChildAt(i2);
                if (this.mOrientationHelper.getDecoratedEnd(child2) <= limit) {
                    if (this.mOrientationHelper.getTransformedEndWithDecoration(child2) <= limit) {
                        i2++;
                    }
                }
                recycleChildren(recycler, 0, i2);
                return;
            }
        }
    }

    private void recycleViewsFromEnd(Recycler recycler, int dt) {
        int childCount = getChildCount();
        if (dt >= 0) {
            int limit = this.mOrientationHelper.getEnd() - dt;
            if (this.mShouldReverseLayout) {
                int i = 0;
                while (i < childCount) {
                    View child = getChildAt(i);
                    if (this.mOrientationHelper.getDecoratedStart(child) >= limit) {
                        if (this.mOrientationHelper.getTransformedStartWithDecoration(child) >= limit) {
                            i++;
                        }
                    }
                    recycleChildren(recycler, 0, i);
                    return;
                }
            }
            int i2 = childCount - 1;
            while (i2 >= 0) {
                View child2 = getChildAt(i2);
                if (this.mOrientationHelper.getDecoratedStart(child2) >= limit) {
                    if (this.mOrientationHelper.getTransformedStartWithDecoration(child2) >= limit) {
                        i2--;
                    }
                }
                recycleChildren(recycler, childCount - 1, i2);
                return;
            }
        }
    }

    private void recycleByLayoutState(Recycler recycler, LayoutState layoutState) {
        if (layoutState.mRecycle) {
            if (!layoutState.mInfinite) {
                if (layoutState.mLayoutDirection == -1) {
                    recycleViewsFromEnd(recycler, layoutState.mScrollingOffset);
                } else {
                    recycleViewsFromStart(recycler, layoutState.mScrollingOffset);
                }
            }
        }
    }

    int fill(Recycler recycler, LayoutState layoutState, State state, boolean stopOnFocusable) {
        int start = layoutState.mAvailable;
        if (layoutState.mScrollingOffset != Integer.MIN_VALUE) {
            if (layoutState.mAvailable < 0) {
                layoutState.mScrollingOffset += layoutState.mAvailable;
            }
            recycleByLayoutState(recycler, layoutState);
        }
        int remainingSpace = layoutState.mAvailable + layoutState.mExtra;
        LayoutChunkResult layoutChunkResult = this.mLayoutChunkResult;
        while (true) {
            if ((!layoutState.mInfinite && remainingSpace <= 0) || !layoutState.hasMore(state)) {
                break;
            }
            layoutChunkResult.resetInternal();
            layoutChunk(recycler, state, layoutState, layoutChunkResult);
            if (!layoutChunkResult.mFinished) {
                layoutState.mOffset += layoutChunkResult.mConsumed * layoutState.mLayoutDirection;
                if (!(layoutChunkResult.mIgnoreConsumed && this.mLayoutState.mScrapList == null && state.isPreLayout())) {
                    layoutState.mAvailable -= layoutChunkResult.mConsumed;
                    remainingSpace -= layoutChunkResult.mConsumed;
                }
                if (layoutState.mScrollingOffset != Integer.MIN_VALUE) {
                    layoutState.mScrollingOffset += layoutChunkResult.mConsumed;
                    if (layoutState.mAvailable < 0) {
                        layoutState.mScrollingOffset += layoutState.mAvailable;
                    }
                    recycleByLayoutState(recycler, layoutState);
                }
                if (stopOnFocusable && layoutChunkResult.mFocusable) {
                    break;
                }
            } else {
                break;
            }
        }
        return start - layoutState.mAvailable;
    }

    void layoutChunk(Recycler recycler, State state, LayoutState layoutState, LayoutChunkResult result) {
        LinearLayoutManager linearLayoutManager = this;
        LayoutState layoutState2 = layoutState;
        LayoutChunkResult layoutChunkResult = result;
        View view = layoutState2.next(recycler);
        if (view == null) {
            layoutChunkResult.mFinished = true;
            return;
        }
        int right;
        int bottom;
        int left;
        int top;
        LayoutParams params = (LayoutParams) view.getLayoutParams();
        if (layoutState2.mScrapList == null) {
            if (linearLayoutManager.mShouldReverseLayout == (layoutState2.mLayoutDirection == -1)) {
                addView(view);
            } else {
                addView(view, 0);
            }
        } else {
            if (linearLayoutManager.mShouldReverseLayout == (layoutState2.mLayoutDirection == -1)) {
                addDisappearingView(view);
            } else {
                addDisappearingView(view, 0);
            }
        }
        measureChildWithMargins(view, 0, 0);
        layoutChunkResult.mConsumed = linearLayoutManager.mOrientationHelper.getDecoratedMeasurement(view);
        int right2;
        int left2;
        if (linearLayoutManager.mOrientation == 1) {
            if (isLayoutRTL()) {
                right2 = getWidth() - getPaddingRight();
                left2 = right2 - linearLayoutManager.mOrientationHelper.getDecoratedMeasurementInOther(view);
            } else {
                left2 = getPaddingLeft();
                right2 = linearLayoutManager.mOrientationHelper.getDecoratedMeasurementInOther(view) + left2;
            }
            if (layoutState2.mLayoutDirection == -1) {
                right = right2;
                bottom = layoutState2.mOffset;
                left = left2;
                top = layoutState2.mOffset - layoutChunkResult.mConsumed;
            } else {
                right = right2;
                top = layoutState2.mOffset;
                left = left2;
                bottom = layoutState2.mOffset + layoutChunkResult.mConsumed;
            }
        } else {
            right2 = getPaddingTop();
            left2 = linearLayoutManager.mOrientationHelper.getDecoratedMeasurementInOther(view) + right2;
            if (layoutState2.mLayoutDirection == -1) {
                top = right2;
                right = layoutState2.mOffset;
                bottom = left2;
                left = layoutState2.mOffset - layoutChunkResult.mConsumed;
            } else {
                top = right2;
                left = layoutState2.mOffset;
                bottom = left2;
                right = layoutState2.mOffset + layoutChunkResult.mConsumed;
            }
        }
        layoutDecoratedWithMargins(view, left, top, right, bottom);
        if (params.isItemRemoved() || params.isItemChanged()) {
            layoutChunkResult.mIgnoreConsumed = true;
        }
        layoutChunkResult.mFocusable = view.hasFocusable();
    }

    boolean shouldMeasureTwice() {
        return (getHeightMode() == NUM || getWidthMode() == NUM || !hasFlexibleChildInBothOrientations()) ? false : true;
    }

    int convertFocusDirectionToLayoutDirection(int focusDirection) {
        int i = -1;
        int i2 = Integer.MIN_VALUE;
        if (focusDirection == 17) {
            if (this.mOrientation != 0) {
                i = Integer.MIN_VALUE;
            }
            return i;
        } else if (focusDirection == 33) {
            if (this.mOrientation != 1) {
                i = Integer.MIN_VALUE;
            }
            return i;
        } else if (focusDirection == 66) {
            if (this.mOrientation == 0) {
                i2 = 1;
            }
            return i2;
        } else if (focusDirection != TsExtractor.TS_STREAM_TYPE_HDMV_DTS) {
            switch (focusDirection) {
                case 1:
                    return (this.mOrientation != 1 && isLayoutRTL()) ? 1 : -1;
                case 2:
                    return (this.mOrientation != 1 && isLayoutRTL()) ? -1 : 1;
                default:
                    return Integer.MIN_VALUE;
            }
        } else {
            if (this.mOrientation == 1) {
                i2 = 1;
            }
            return i2;
        }
    }

    private View getChildClosestToStart() {
        return getChildAt(this.mShouldReverseLayout ? getChildCount() - 1 : 0);
    }

    private View getChildClosestToEnd() {
        return getChildAt(this.mShouldReverseLayout ? 0 : getChildCount() - 1);
    }

    private View findFirstVisibleChildClosestToStart(boolean completelyVisible, boolean acceptPartiallyVisible) {
        if (this.mShouldReverseLayout) {
            return findOneVisibleChild(getChildCount() - 1, -1, completelyVisible, acceptPartiallyVisible);
        }
        return findOneVisibleChild(0, getChildCount(), completelyVisible, acceptPartiallyVisible);
    }

    private View findFirstVisibleChildClosestToEnd(boolean completelyVisible, boolean acceptPartiallyVisible) {
        if (this.mShouldReverseLayout) {
            return findOneVisibleChild(0, getChildCount(), completelyVisible, acceptPartiallyVisible);
        }
        return findOneVisibleChild(getChildCount() - 1, -1, completelyVisible, acceptPartiallyVisible);
    }

    private View findReferenceChildClosestToEnd(Recycler recycler, State state) {
        if (this.mShouldReverseLayout) {
            return findFirstReferenceChild(recycler, state);
        }
        return findLastReferenceChild(recycler, state);
    }

    private View findReferenceChildClosestToStart(Recycler recycler, State state) {
        if (this.mShouldReverseLayout) {
            return findLastReferenceChild(recycler, state);
        }
        return findFirstReferenceChild(recycler, state);
    }

    private View findFirstReferenceChild(Recycler recycler, State state) {
        return findReferenceChild(recycler, state, 0, getChildCount(), state.getItemCount());
    }

    private View findLastReferenceChild(Recycler recycler, State state) {
        return findReferenceChild(recycler, state, getChildCount() - 1, -1, state.getItemCount());
    }

    View findReferenceChild(Recycler recycler, State state, int start, int end, int itemCount) {
        ensureLayoutState();
        View outOfBoundsMatch = null;
        int boundsStart = this.mOrientationHelper.getStartAfterPadding();
        int boundsEnd = this.mOrientationHelper.getEndAfterPadding();
        int diff = end > start ? 1 : -1;
        View invalidMatch = null;
        for (int i = start; i != end; i += diff) {
            View view = getChildAt(i);
            int position = getPosition(view);
            if (position >= 0 && position < itemCount) {
                if (!((LayoutParams) view.getLayoutParams()).isItemRemoved()) {
                    if (this.mOrientationHelper.getDecoratedStart(view) < boundsEnd) {
                        if (this.mOrientationHelper.getDecoratedEnd(view) >= boundsStart) {
                            return view;
                        }
                    }
                    if (outOfBoundsMatch == null) {
                        outOfBoundsMatch = view;
                    }
                } else if (invalidMatch == null) {
                    invalidMatch = view;
                }
            }
        }
        return outOfBoundsMatch != null ? outOfBoundsMatch : invalidMatch;
    }

    private View findPartiallyOrCompletelyInvisibleChildClosestToEnd(Recycler recycler, State state) {
        if (this.mShouldReverseLayout) {
            return findFirstPartiallyOrCompletelyInvisibleChild(recycler, state);
        }
        return findLastPartiallyOrCompletelyInvisibleChild(recycler, state);
    }

    private View findPartiallyOrCompletelyInvisibleChildClosestToStart(Recycler recycler, State state) {
        if (this.mShouldReverseLayout) {
            return findLastPartiallyOrCompletelyInvisibleChild(recycler, state);
        }
        return findFirstPartiallyOrCompletelyInvisibleChild(recycler, state);
    }

    private View findFirstPartiallyOrCompletelyInvisibleChild(Recycler recycler, State state) {
        return findOnePartiallyOrCompletelyInvisibleChild(0, getChildCount());
    }

    private View findLastPartiallyOrCompletelyInvisibleChild(Recycler recycler, State state) {
        return findOnePartiallyOrCompletelyInvisibleChild(getChildCount() - 1, -1);
    }

    public int findFirstVisibleItemPosition() {
        View child = findOneVisibleChild(0, getChildCount(), false, true);
        return child == null ? -1 : getPosition(child);
    }

    public int findFirstCompletelyVisibleItemPosition() {
        View child = findOneVisibleChild(0, getChildCount(), true, false);
        return child == null ? -1 : getPosition(child);
    }

    public int findLastVisibleItemPosition() {
        View child = findOneVisibleChild(getChildCount() - 1, -1, false, true);
        if (child == null) {
            return -1;
        }
        return getPosition(child);
    }

    public int findLastCompletelyVisibleItemPosition() {
        View child = findOneVisibleChild(getChildCount() - 1, -1, true, false);
        if (child == null) {
            return -1;
        }
        return getPosition(child);
    }

    View findOneVisibleChild(int fromIndex, int toIndex, boolean completelyVisible, boolean acceptPartiallyVisible) {
        int preferredBoundsFlag;
        ensureLayoutState();
        int acceptableBoundsFlag = 0;
        if (completelyVisible) {
            preferredBoundsFlag = 24579;
        } else {
            preferredBoundsFlag = 320;
        }
        if (acceptPartiallyVisible) {
            acceptableBoundsFlag = 320;
        }
        if (this.mOrientation == 0) {
            return this.mHorizontalBoundCheck.findOneViewWithinBoundFlags(fromIndex, toIndex, preferredBoundsFlag, acceptableBoundsFlag);
        }
        return this.mVerticalBoundCheck.findOneViewWithinBoundFlags(fromIndex, toIndex, preferredBoundsFlag, acceptableBoundsFlag);
    }

    View findOnePartiallyOrCompletelyInvisibleChild(int fromIndex, int toIndex) {
        ensureLayoutState();
        int next = toIndex > fromIndex ? 1 : toIndex < fromIndex ? -1 : 0;
        if (next == 0) {
            return getChildAt(fromIndex);
        }
        int preferredBoundsFlag;
        int acceptableBoundsFlag;
        View findOneViewWithinBoundFlags;
        if (this.mOrientationHelper.getDecoratedStart(getChildAt(fromIndex)) < this.mOrientationHelper.getStartAfterPadding()) {
            preferredBoundsFlag = 16644;
            acceptableBoundsFlag = 16388;
        } else {
            preferredBoundsFlag = 4161;
            acceptableBoundsFlag = 4097;
        }
        if (this.mOrientation == 0) {
            findOneViewWithinBoundFlags = this.mHorizontalBoundCheck.findOneViewWithinBoundFlags(fromIndex, toIndex, preferredBoundsFlag, acceptableBoundsFlag);
        } else {
            findOneViewWithinBoundFlags = this.mVerticalBoundCheck.findOneViewWithinBoundFlags(fromIndex, toIndex, preferredBoundsFlag, acceptableBoundsFlag);
        }
        return findOneViewWithinBoundFlags;
    }

    public View onFocusSearchFailed(View focused, int focusDirection, Recycler recycler, State state) {
        resolveShouldLayoutReverse();
        if (getChildCount() == 0) {
            return null;
        }
        int layoutDir = convertFocusDirectionToLayoutDirection(focusDirection);
        if (layoutDir == Integer.MIN_VALUE) {
            return null;
        }
        View nextCandidate;
        View nextFocus;
        ensureLayoutState();
        ensureLayoutState();
        updateLayoutState(layoutDir, (int) (NUM * ((float) this.mOrientationHelper.getTotalSpace())), false, state);
        this.mLayoutState.mScrollingOffset = Integer.MIN_VALUE;
        this.mLayoutState.mRecycle = false;
        fill(recycler, this.mLayoutState, state, true);
        if (layoutDir == -1) {
            nextCandidate = findPartiallyOrCompletelyInvisibleChildClosestToStart(recycler, state);
        } else {
            nextCandidate = findPartiallyOrCompletelyInvisibleChildClosestToEnd(recycler, state);
        }
        if (layoutDir == -1) {
            nextFocus = getChildClosestToStart();
        } else {
            nextFocus = getChildClosestToEnd();
        }
        if (!nextFocus.hasFocusable()) {
            return nextCandidate;
        }
        if (nextCandidate == null) {
            return null;
        }
        return nextFocus;
    }

    private void logChildren() {
        Log.d(TAG, "internal representation of views on the screen");
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("item ");
            stringBuilder.append(getPosition(child));
            stringBuilder.append(", coord:");
            stringBuilder.append(this.mOrientationHelper.getDecoratedStart(child));
            Log.d(str, stringBuilder.toString());
        }
        Log.d(TAG, "==============");
    }

    void validateChildOrder() {
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("validating child count ");
        stringBuilder.append(getChildCount());
        Log.d(str, stringBuilder.toString());
        if (getChildCount() >= 1) {
            boolean z = false;
            int lastPos = getPosition(getChildAt(0));
            int lastScreenLoc = this.mOrientationHelper.getDecoratedStart(getChildAt(0));
            int i;
            View child;
            int pos;
            int screenLoc;
            StringBuilder stringBuilder2;
            if (this.mShouldReverseLayout) {
                i = 1;
                while (i < getChildCount()) {
                    child = getChildAt(i);
                    pos = getPosition(child);
                    screenLoc = this.mOrientationHelper.getDecoratedStart(child);
                    if (pos < lastPos) {
                        logChildren();
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("detected invalid position. loc invalid? ");
                        if (screenLoc < lastScreenLoc) {
                            z = true;
                        }
                        stringBuilder2.append(z);
                        throw new RuntimeException(stringBuilder2.toString());
                    } else if (screenLoc > lastScreenLoc) {
                        logChildren();
                        throw new RuntimeException("detected invalid location");
                    } else {
                        i++;
                    }
                }
            } else {
                i = 1;
                while (i < getChildCount()) {
                    child = getChildAt(i);
                    pos = getPosition(child);
                    screenLoc = this.mOrientationHelper.getDecoratedStart(child);
                    if (pos < lastPos) {
                        logChildren();
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("detected invalid position. loc invalid? ");
                        if (screenLoc < lastScreenLoc) {
                            z = true;
                        }
                        stringBuilder2.append(z);
                        throw new RuntimeException(stringBuilder2.toString());
                    } else if (screenLoc < lastScreenLoc) {
                        logChildren();
                        throw new RuntimeException("detected invalid location");
                    } else {
                        i++;
                    }
                }
            }
        }
    }

    public boolean supportsPredictiveItemAnimations() {
        return this.mPendingSavedState == null && this.mLastStackFromEnd == this.mStackFromEnd;
    }

    public void prepareForDrop(View view, View target, int x, int y) {
        assertNotInLayoutOrScroll("Cannot drop a view during a scroll or layout calculation");
        ensureLayoutState();
        resolveShouldLayoutReverse();
        int myPos = getPosition(view);
        int targetPos = getPosition(target);
        int dropDirection = myPos < targetPos ? 1 : -1;
        if (this.mShouldReverseLayout) {
            if (dropDirection == 1) {
                scrollToPositionWithOffset(targetPos, this.mOrientationHelper.getEndAfterPadding() - (this.mOrientationHelper.getDecoratedStart(target) + this.mOrientationHelper.getDecoratedMeasurement(view)));
            } else {
                scrollToPositionWithOffset(targetPos, this.mOrientationHelper.getEndAfterPadding() - this.mOrientationHelper.getDecoratedEnd(target));
            }
        } else if (dropDirection == -1) {
            scrollToPositionWithOffset(targetPos, this.mOrientationHelper.getDecoratedStart(target));
        } else {
            scrollToPositionWithOffset(targetPos, this.mOrientationHelper.getDecoratedEnd(target) - this.mOrientationHelper.getDecoratedMeasurement(view));
        }
    }
}
