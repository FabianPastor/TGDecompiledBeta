package org.telegram.messenger.support.widget;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.View.MeasureSpec;
import java.util.ArrayList;
import java.util.Arrays;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.Recycler;
import org.telegram.messenger.support.widget.RecyclerView.State;

public class GridLayoutManagerFixed extends GridLayoutManager {
    private ArrayList<View> additionalViews = new ArrayList(4);
    private boolean canScrollVertically = true;

    public GridLayoutManagerFixed(Context context, int spanCount) {
        super(context, spanCount);
    }

    public GridLayoutManagerFixed(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    protected boolean hasSiblingChild(int position) {
        return false;
    }

    public void setCanScrollVertically(boolean value) {
        this.canScrollVertically = value;
    }

    public boolean canScrollVertically() {
        return this.canScrollVertically;
    }

    protected void recycleViewsFromStart(Recycler recycler, int dt) {
        if (dt >= 0) {
            int childCount = getChildCount();
            if (this.mShouldReverseLayout) {
                int i = childCount - 1;
                while (i >= 0) {
                    View child = getChildAt(i);
                    if (child.getBottom() + ((LayoutParams) child.getLayoutParams()).bottomMargin <= dt) {
                        if (child.getTop() + child.getHeight() <= dt) {
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
                if (this.mOrientationHelper.getDecoratedEnd(child2) <= dt) {
                    if (this.mOrientationHelper.getTransformedEndWithDecoration(child2) <= dt) {
                        i2++;
                    }
                }
                recycleChildren(recycler, 0, i2);
                return;
            }
        }
    }

    protected int[] calculateItemBorders(int[] cachedBorders, int spanCount, int totalSpace) {
        int i = 1;
        if (!(cachedBorders != null && cachedBorders.length == spanCount + 1 && cachedBorders[cachedBorders.length - 1] == totalSpace)) {
            cachedBorders = new int[(spanCount + 1)];
        }
        cachedBorders[0] = 0;
        while (i <= spanCount) {
            cachedBorders[i] = (int) Math.ceil((double) ((((float) i) / ((float) spanCount)) * ((float) totalSpace)));
            i++;
        }
        return cachedBorders;
    }

    public boolean shouldLayoutChildFromOpositeSide(View child) {
        return false;
    }

    protected void measureChild(View view, int otherDirParentSpecMode, boolean alreadyMeasured) {
        GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        Rect decorInsets = lp.mDecorInsets;
        int horizontalInsets = ((decorInsets.left + decorInsets.right) + lp.leftMargin) + lp.rightMargin;
        measureChildWithDecorationsAndMargin(view, LayoutManager.getChildMeasureSpec(this.mCachedBorders[lp.mSpanSize], otherDirParentSpecMode, horizontalInsets, lp.width, false), LayoutManager.getChildMeasureSpec(this.mOrientationHelper.getTotalSpace(), getHeightMode(), ((decorInsets.top + decorInsets.bottom) + lp.topMargin) + lp.bottomMargin, lp.height, true), alreadyMeasured);
    }

    void layoutChunk(Recycler recycler, State state, LayoutState layoutState, LayoutChunkResult result) {
        int backupPosition;
        int a;
        int otherDirSpecMode;
        Recycler recycler2 = recycler;
        State state2 = state;
        LayoutState layoutState2 = layoutState;
        LayoutChunkResult layoutChunkResult = result;
        int otherDirSpecMode2 = this.mOrientationHelper.getModeInOther();
        boolean z = false;
        int i = 1;
        boolean layingOutInPrimaryDirection = layoutState2.mItemDirection == 1;
        boolean working = true;
        layoutChunkResult.mConsumed = 0;
        int startPosition = layoutState2.mCurrentPosition;
        if (layoutState2.mLayoutDirection != -1 && hasSiblingChild(layoutState2.mCurrentPosition) && findViewByPosition(layoutState2.mCurrentPosition + 1) == null) {
            if (hasSiblingChild(layoutState2.mCurrentPosition + 1)) {
                layoutState2.mCurrentPosition += 3;
            } else {
                layoutState2.mCurrentPosition += 2;
            }
            backupPosition = layoutState2.mCurrentPosition;
            a = layoutState2.mCurrentPosition;
            while (a > startPosition) {
                View view = layoutState2.next(recycler2);
                r6.additionalViews.add(view);
                if (a != backupPosition) {
                    calculateItemDecorationsForChild(view, r6.mDecorInsets);
                    measureChild(view, otherDirSpecMode2, z);
                    int size = r6.mOrientationHelper.getDecoratedMeasurement(view);
                    layoutState2.mOffset -= size;
                    layoutState2.mAvailable += size;
                }
                a--;
                z = false;
            }
            layoutState2.mCurrentPosition = backupPosition;
        }
        while (working) {
            int remainingSpan = r6.mSpanCount;
            working = r6.additionalViews.isEmpty() ^ i;
            size = layoutState2.mCurrentPosition;
            boolean working2 = working;
            int count = 0;
            int consumedSpanCount = 0;
            while (true) {
                int firstPositionStart = size;
                if (count >= r6.mSpanCount || !layoutState2.hasMore(state2) || remainingSpan <= 0) {
                } else {
                    int pos = layoutState2.mCurrentPosition;
                    backupPosition = getSpanSize(recycler2, state2, pos);
                    remainingSpan -= backupPosition;
                    if (remainingSpan < 0) {
                        break;
                    }
                    View view2;
                    if (r6.additionalViews.isEmpty()) {
                        view2 = layoutState2.next(recycler2);
                    } else {
                        view2 = (View) r6.additionalViews.get(0);
                        r6.additionalViews.remove(0);
                        layoutState2.mCurrentPosition--;
                    }
                    if (view2 == null) {
                        break;
                    }
                    consumedSpanCount += backupPosition;
                    r6.mSet[count] = view2;
                    count++;
                    if (layoutState2.mLayoutDirection == -1 && remainingSpan <= 0 && hasSiblingChild(pos)) {
                        working2 = true;
                    }
                    size = firstPositionStart;
                    int i2 = remainingSpan;
                }
                if (count != 0) {
                    layoutChunkResult.mFinished = true;
                    return;
                }
                int maxSize;
                boolean z2;
                int bottom;
                int i3;
                int right;
                int left;
                int top;
                View view3;
                float maxSizeInOther = 0.0f;
                int startPosition2 = startPosition;
                assignSpans(recycler2, state2, count, consumedSpanCount, layingOutInPrimaryDirection);
                startPosition = 0;
                for (pos = 0; pos < count; pos++) {
                    boolean z3;
                    View view4 = r6.mSet[pos];
                    if (layoutState2.mScrapList == null) {
                        z3 = false;
                        if (layingOutInPrimaryDirection) {
                            addDisappearingView(view4, 0);
                        } else {
                            addDisappearingView(view4);
                        }
                    } else if (layingOutInPrimaryDirection) {
                        z3 = false;
                        addView(view4, 0);
                    } else {
                        addView(view4);
                        z3 = false;
                    }
                    calculateItemDecorationsForChild(view4, r6.mDecorInsets);
                    measureChild(view4, otherDirSpecMode2, z3);
                    a = r6.mOrientationHelper.getDecoratedMeasurement(view4);
                    if (a > startPosition) {
                        startPosition = a;
                    }
                    float otherSize = (1.0f * ((float) r6.mOrientationHelper.getDecoratedMeasurementInOther(view4))) / ((float) ((GridLayoutManager.LayoutParams) view4.getLayoutParams()).mSpanSize);
                    if (otherSize > maxSizeInOther) {
                        maxSizeInOther = otherSize;
                    }
                }
                pos = 0;
                while (pos < count) {
                    view4 = r6.mSet[pos];
                    if (r6.mOrientationHelper.getDecoratedMeasurement(view4) != startPosition) {
                        GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view4.getLayoutParams();
                        Rect decorInsets = lp.mDecorInsets;
                        measureChildWithDecorationsAndMargin(view4, LayoutManager.getChildMeasureSpec(r6.mCachedBorders[lp.mSpanSize], NUM, ((decorInsets.left + decorInsets.right) + lp.leftMargin) + lp.rightMargin, lp.width, false), MeasureSpec.makeMeasureSpec(startPosition - (((decorInsets.top + decorInsets.bottom) + lp.topMargin) + lp.bottomMargin), NUM), true);
                    }
                    pos++;
                    recycler2 = recycler;
                    state2 = state;
                }
                boolean fromOpositeSide = shouldLayoutChildFromOpositeSide(r6.mSet[0]);
                if ((fromOpositeSide || layoutState2.mLayoutDirection != -1) && (fromOpositeSide || layoutState2.mLayoutDirection != 1)) {
                    maxSize = startPosition;
                    z2 = fromOpositeSide;
                    if (layoutState2.mLayoutDirection != -1) {
                        pos = layoutState2.mOffset - layoutChunkResult.mConsumed;
                        backupPosition = pos - maxSize;
                        a = getWidth();
                        bottom = pos;
                        i3 = backupPosition;
                    } else {
                        pos = layoutState2.mOffset + layoutChunkResult.mConsumed;
                        i3 = pos;
                        bottom = pos + maxSize;
                        a = 0;
                    }
                    backupPosition = a;
                    pos = 0;
                    while (true) {
                        i = pos;
                        if (i < count) {
                            break;
                        }
                        startPosition = r6.mSet[i];
                        GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) startPosition.getLayoutParams();
                        right = r6.mOrientationHelper.getDecoratedMeasurementInOther(startPosition);
                        if (layoutState2.mLayoutDirection == -1) {
                            backupPosition -= right;
                        }
                        left = backupPosition;
                        remainingSpan = i3;
                        top = i3;
                        i3 = params;
                        otherDirSpecMode = otherDirSpecMode2;
                        view3 = startPosition;
                        layoutDecoratedWithMargins(startPosition, left, remainingSpan, left + right, bottom);
                        if (layoutState2.mLayoutDirection != -1) {
                            left += right;
                        }
                        backupPosition = left;
                        if (!i3.isItemRemoved()) {
                            if (i3.isItemChanged()) {
                                layoutChunkResult.mFocusable |= view3.hasFocusable();
                                pos = i + 1;
                                i3 = top;
                                otherDirSpecMode2 = otherDirSpecMode;
                            }
                        }
                        layoutChunkResult.mIgnoreConsumed = true;
                        layoutChunkResult.mFocusable |= view3.hasFocusable();
                        pos = i + 1;
                        i3 = top;
                        otherDirSpecMode2 = otherDirSpecMode;
                    }
                    otherDirSpecMode = otherDirSpecMode2;
                    remainingSpan = 1;
                } else {
                    if (layoutState2.mLayoutDirection == -1) {
                        pos = layoutState2.mOffset - layoutChunkResult.mConsumed;
                        a = 0;
                        right = pos;
                        i = pos - startPosition;
                    } else {
                        pos = layoutState2.mOffset + layoutChunkResult.mConsumed;
                        backupPosition = pos + startPosition;
                        a = getWidth();
                        i = pos;
                        right = backupPosition;
                    }
                    pos = a;
                    backupPosition = count - 1;
                    while (true) {
                        left = backupPosition;
                        if (left < 0) {
                            break;
                        }
                        View view5 = r6.mSet[left];
                        GridLayoutManager.LayoutParams params2 = (GridLayoutManager.LayoutParams) view5.getLayoutParams();
                        int right2 = r6.mOrientationHelper.getDecoratedMeasurementInOther(view5);
                        if (layoutState2.mLayoutDirection == 1) {
                            pos -= right2;
                        }
                        int left2 = pos;
                        GridLayoutManager.LayoutParams params3 = params2;
                        z2 = fromOpositeSide;
                        View view6 = view5;
                        maxSize = startPosition;
                        layoutDecoratedWithMargins(view5, left2, i, left2 + right2, right);
                        if (layoutState2.mLayoutDirection == -1) {
                            left2 += right2;
                        }
                        pos = left2;
                        if (params3.isItemRemoved() || params3.isItemChanged()) {
                            layoutChunkResult.mIgnoreConsumed = true;
                        }
                        layoutChunkResult.mFocusable |= view6.hasFocusable();
                        backupPosition = left - 1;
                        startPosition = maxSize;
                        fromOpositeSide = z2;
                    }
                    maxSize = startPosition;
                    z2 = fromOpositeSide;
                    otherDirSpecMode = otherDirSpecMode2;
                    remainingSpan = 1;
                }
                layoutChunkResult.mConsumed += maxSize;
                Arrays.fill(r6.mSet, null);
                i = remainingSpan;
                working = working2;
                startPosition = startPosition2;
                otherDirSpecMode2 = otherDirSpecMode;
                recycler2 = recycler;
                state2 = state;
            }
            if (count != 0) {
                float maxSizeInOther2 = 0.0f;
                int startPosition22 = startPosition;
                assignSpans(recycler2, state2, count, consumedSpanCount, layingOutInPrimaryDirection);
                startPosition = 0;
                for (pos = 0; pos < count; pos++) {
                    View view42 = r6.mSet[pos];
                    if (layoutState2.mScrapList == null) {
                        z3 = false;
                        if (layingOutInPrimaryDirection) {
                            addDisappearingView(view42, 0);
                        } else {
                            addDisappearingView(view42);
                        }
                    } else if (layingOutInPrimaryDirection) {
                        z3 = false;
                        addView(view42, 0);
                    } else {
                        addView(view42);
                        z3 = false;
                    }
                    calculateItemDecorationsForChild(view42, r6.mDecorInsets);
                    measureChild(view42, otherDirSpecMode2, z3);
                    a = r6.mOrientationHelper.getDecoratedMeasurement(view42);
                    if (a > startPosition) {
                        startPosition = a;
                    }
                    float otherSize2 = (1.0f * ((float) r6.mOrientationHelper.getDecoratedMeasurementInOther(view42))) / ((float) ((GridLayoutManager.LayoutParams) view42.getLayoutParams()).mSpanSize);
                    if (otherSize2 > maxSizeInOther2) {
                        maxSizeInOther2 = otherSize2;
                    }
                }
                pos = 0;
                while (pos < count) {
                    view42 = r6.mSet[pos];
                    if (r6.mOrientationHelper.getDecoratedMeasurement(view42) != startPosition) {
                        GridLayoutManager.LayoutParams lp2 = (GridLayoutManager.LayoutParams) view42.getLayoutParams();
                        Rect decorInsets2 = lp2.mDecorInsets;
                        measureChildWithDecorationsAndMargin(view42, LayoutManager.getChildMeasureSpec(r6.mCachedBorders[lp2.mSpanSize], NUM, ((decorInsets2.left + decorInsets2.right) + lp2.leftMargin) + lp2.rightMargin, lp2.width, false), MeasureSpec.makeMeasureSpec(startPosition - (((decorInsets2.top + decorInsets2.bottom) + lp2.topMargin) + lp2.bottomMargin), NUM), true);
                    }
                    pos++;
                    recycler2 = recycler;
                    state2 = state;
                }
                boolean fromOpositeSide2 = shouldLayoutChildFromOpositeSide(r6.mSet[0]);
                if (fromOpositeSide2) {
                }
                maxSize = startPosition;
                z2 = fromOpositeSide2;
                if (layoutState2.mLayoutDirection != -1) {
                    pos = layoutState2.mOffset + layoutChunkResult.mConsumed;
                    i3 = pos;
                    bottom = pos + maxSize;
                    a = 0;
                } else {
                    pos = layoutState2.mOffset - layoutChunkResult.mConsumed;
                    backupPosition = pos - maxSize;
                    a = getWidth();
                    bottom = pos;
                    i3 = backupPosition;
                }
                backupPosition = a;
                pos = 0;
                while (true) {
                    i = pos;
                    if (i < count) {
                        break;
                    }
                    startPosition = r6.mSet[i];
                    GridLayoutManager.LayoutParams params4 = (GridLayoutManager.LayoutParams) startPosition.getLayoutParams();
                    right = r6.mOrientationHelper.getDecoratedMeasurementInOther(startPosition);
                    if (layoutState2.mLayoutDirection == -1) {
                        backupPosition -= right;
                    }
                    left = backupPosition;
                    remainingSpan = i3;
                    top = i3;
                    i3 = params4;
                    otherDirSpecMode = otherDirSpecMode2;
                    view3 = startPosition;
                    layoutDecoratedWithMargins(startPosition, left, remainingSpan, left + right, bottom);
                    if (layoutState2.mLayoutDirection != -1) {
                        left += right;
                    }
                    backupPosition = left;
                    if (i3.isItemRemoved()) {
                        if (i3.isItemChanged()) {
                            layoutChunkResult.mFocusable |= view3.hasFocusable();
                            pos = i + 1;
                            i3 = top;
                            otherDirSpecMode2 = otherDirSpecMode;
                        }
                    }
                    layoutChunkResult.mIgnoreConsumed = true;
                    layoutChunkResult.mFocusable |= view3.hasFocusable();
                    pos = i + 1;
                    i3 = top;
                    otherDirSpecMode2 = otherDirSpecMode;
                }
                otherDirSpecMode = otherDirSpecMode2;
                remainingSpan = 1;
                layoutChunkResult.mConsumed += maxSize;
                Arrays.fill(r6.mSet, null);
                i = remainingSpan;
                working = working2;
                startPosition = startPosition22;
                otherDirSpecMode2 = otherDirSpecMode;
                recycler2 = recycler;
                state2 = state;
            } else {
                layoutChunkResult.mFinished = true;
                return;
            }
        }
        otherDirSpecMode = otherDirSpecMode2;
    }
}
