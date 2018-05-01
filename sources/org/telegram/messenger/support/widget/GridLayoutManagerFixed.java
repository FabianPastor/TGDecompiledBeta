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

    protected boolean hasSiblingChild(int i) {
        return false;
    }

    public boolean shouldLayoutChildFromOpositeSide(View view) {
        return false;
    }

    public GridLayoutManagerFixed(Context context, int i) {
        super(context, i);
    }

    public GridLayoutManagerFixed(Context context, int i, int i2, boolean z) {
        super(context, i, i2, z);
    }

    public void setCanScrollVertically(boolean z) {
        this.canScrollVertically = z;
    }

    public boolean canScrollVertically() {
        return this.canScrollVertically;
    }

    protected void recycleViewsFromStart(Recycler recycler, int i) {
        if (i >= 0) {
            int childCount = getChildCount();
            if (this.mShouldReverseLayout) {
                childCount--;
                int i2 = childCount;
                while (i2 >= 0) {
                    View childAt = getChildAt(i2);
                    if (childAt.getBottom() + ((LayoutParams) childAt.getLayoutParams()).bottomMargin <= i) {
                        if (childAt.getTop() + childAt.getHeight() <= i) {
                            i2--;
                        }
                    }
                    recycleChildren(recycler, childCount, i2);
                    return;
                }
            }
            int i3 = 0;
            while (i3 < childCount) {
                View childAt2 = getChildAt(i3);
                if (this.mOrientationHelper.getDecoratedEnd(childAt2) <= i) {
                    if (this.mOrientationHelper.getTransformedEndWithDecoration(childAt2) <= i) {
                        i3++;
                    }
                }
                recycleChildren(recycler, 0, i3);
                return;
            }
        }
    }

    protected int[] calculateItemBorders(int[] iArr, int i, int i2) {
        int i3 = 1;
        if (!(iArr != null && iArr.length == i + 1 && iArr[iArr.length - 1] == i2)) {
            iArr = new int[(i + 1)];
        }
        iArr[0] = 0;
        while (i3 <= i) {
            iArr[i3] = (int) Math.ceil((double) ((((float) i3) / ((float) i)) * ((float) i2)));
            i3++;
        }
        return iArr;
    }

    protected void measureChild(View view, int i, boolean z) {
        GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        Rect rect = layoutParams.mDecorInsets;
        int i2 = ((rect.left + rect.right) + layoutParams.leftMargin) + layoutParams.rightMargin;
        measureChildWithDecorationsAndMargin(view, LayoutManager.getChildMeasureSpec(this.mCachedBorders[layoutParams.mSpanSize], i, i2, layoutParams.width, false), LayoutManager.getChildMeasureSpec(this.mOrientationHelper.getTotalSpace(), getHeightMode(), ((rect.top + rect.bottom) + layoutParams.topMargin) + layoutParams.bottomMargin, layoutParams.height, true), z);
    }

    void layoutChunk(Recycler recycler, State state, LayoutState layoutState, LayoutChunkResult layoutChunkResult) {
        int i;
        int i2;
        View next;
        Recycler recycler2 = recycler;
        State state2 = state;
        LayoutState layoutState2 = layoutState;
        LayoutChunkResult layoutChunkResult2 = layoutChunkResult;
        int modeInOther = this.mOrientationHelper.getModeInOther();
        boolean z = false;
        boolean z2 = true;
        boolean z3 = layoutState2.mItemDirection == 1;
        layoutChunkResult2.mConsumed = 0;
        int i3 = layoutState2.mCurrentPosition;
        if (layoutState2.mLayoutDirection != -1 && hasSiblingChild(layoutState2.mCurrentPosition) && findViewByPosition(layoutState2.mCurrentPosition + 1) == null) {
            if (hasSiblingChild(layoutState2.mCurrentPosition + 1)) {
                layoutState2.mCurrentPosition += 3;
            } else {
                layoutState2.mCurrentPosition += 2;
            }
            i = layoutState2.mCurrentPosition;
            for (i2 = layoutState2.mCurrentPosition; i2 > i3; i2--) {
                next = layoutState2.next(recycler2);
                r6.additionalViews.add(next);
                if (i2 != i) {
                    calculateItemDecorationsForChild(next, r6.mDecorInsets);
                    measureChild(next, modeInOther, false);
                    int decoratedMeasurement = r6.mOrientationHelper.getDecoratedMeasurement(next);
                    layoutState2.mOffset -= decoratedMeasurement;
                    layoutState2.mAvailable += decoratedMeasurement;
                }
            }
            layoutState2.mCurrentPosition = i;
        }
        boolean z4 = true;
        while (z4) {
            i3 = r6.mSpanCount;
            boolean isEmpty = r6.additionalViews.isEmpty() ^ z2;
            i2 = layoutState2.mCurrentPosition;
            boolean z5 = isEmpty;
            int i4 = z;
            int i5 = i4;
            while (i5 < r6.mSpanCount && layoutState2.hasMore(state2) && i3 > 0) {
                i = layoutState2.mCurrentPosition;
                i2 = getSpanSize(recycler2, state2, i);
                i3 -= i2;
                if (i3 < 0) {
                    break;
                }
                if (r6.additionalViews.isEmpty()) {
                    next = layoutState2.next(recycler2);
                } else {
                    next = (View) r6.additionalViews.get(z);
                    r6.additionalViews.remove(z);
                    layoutState2.mCurrentPosition -= z2;
                }
                if (next == null) {
                    break;
                }
                i4 += i2;
                r6.mSet[i5] = next;
                i5++;
                if (layoutState2.mLayoutDirection == -1 && i3 <= 0 && hasSiblingChild(i)) {
                    z5 = z2;
                }
            }
            if (i5 == 0) {
                layoutChunkResult2.mFinished = z2;
                return;
            }
            boolean z6;
            int i6;
            int i7;
            int i8;
            int i9;
            GridLayoutManager.LayoutParams layoutParams;
            int i10;
            int i11 = i5;
            assignSpans(recycler2, state2, i5, i4, z3);
            i3 = z;
            i5 = i3;
            float f = 0.0f;
            while (i3 < i11) {
                View view = r6.mSet[i3];
                if (layoutState2.mScrapList == null) {
                    if (z3) {
                        addView(view);
                    } else {
                        addView(view, z);
                    }
                } else if (z3) {
                    addDisappearingView(view);
                } else {
                    addDisappearingView(view, z);
                }
                calculateItemDecorationsForChild(view, r6.mDecorInsets);
                measureChild(view, modeInOther, z);
                i2 = r6.mOrientationHelper.getDecoratedMeasurement(view);
                if (i2 > i5) {
                    i5 = i2;
                }
                float decoratedMeasurementInOther = (1.0f * ((float) r6.mOrientationHelper.getDecoratedMeasurementInOther(view))) / ((float) ((GridLayoutManager.LayoutParams) view.getLayoutParams()).mSpanSize);
                if (decoratedMeasurementInOther > f) {
                    f = decoratedMeasurementInOther;
                }
                i3++;
            }
            i3 = z;
            while (i3 < i11) {
                view = r6.mSet[i3];
                if (r6.mOrientationHelper.getDecoratedMeasurement(view) != i5) {
                    GridLayoutManager.LayoutParams layoutParams2 = (GridLayoutManager.LayoutParams) view.getLayoutParams();
                    Rect rect = layoutParams2.mDecorInsets;
                    z6 = false;
                    measureChildWithDecorationsAndMargin(view, LayoutManager.getChildMeasureSpec(r6.mCachedBorders[layoutParams2.mSpanSize], NUM, ((rect.left + rect.right) + layoutParams2.leftMargin) + layoutParams2.rightMargin, layoutParams2.width, false), MeasureSpec.makeMeasureSpec(i5 - (((rect.top + rect.bottom) + layoutParams2.topMargin) + layoutParams2.bottomMargin), NUM), true);
                } else {
                    z6 = z;
                }
                i3++;
                z = z6;
                recycler2 = recycler;
            }
            z6 = z;
            z4 = shouldLayoutChildFromOpositeSide(r6.mSet[z6]);
            if (z4) {
                i2 = -1;
                if (layoutState2.mLayoutDirection != -1) {
                }
                if (layoutState2.mLayoutDirection != i2) {
                    i3 = layoutState2.mOffset - layoutChunkResult2.mConsumed;
                    i6 = i3;
                    i7 = i3 - i5;
                    i8 = z6;
                } else {
                    i3 = layoutState2.mOffset + layoutChunkResult2.mConsumed;
                    i = i3 + i5;
                    i8 = getWidth();
                    i7 = i3;
                    i6 = i;
                }
                i9 = i8;
                i8 = i11 - 1;
                i3 = i9;
                while (i8 >= 0) {
                    View view2 = r6.mSet[i8];
                    GridLayoutManager.LayoutParams layoutParams3 = (GridLayoutManager.LayoutParams) view2.getLayoutParams();
                    i = r6.mOrientationHelper.getDecoratedMeasurementInOther(view2);
                    if (layoutState2.mLayoutDirection == 1) {
                        i3 -= i;
                    }
                    int i12 = i3;
                    int i13 = i12 + i;
                    layoutParams = layoutParams3;
                    i10 = i5;
                    layoutDecoratedWithMargins(view2, i12, i7, i13, i6);
                    i3 = layoutState2.mLayoutDirection != -1 ? i13 : i12;
                    if (!layoutParams.isItemRemoved() || layoutParams.isItemChanged()) {
                        layoutChunkResult2.mIgnoreConsumed = true;
                    }
                    layoutChunkResult2.mFocusable |= view2.hasFocusable();
                    i8--;
                    i5 = i10;
                }
                i10 = i5;
                layoutChunkResult2.mConsumed += i10;
                Arrays.fill(r6.mSet, null);
                i11 = -1;
                z2 = true;
                z4 = z5;
                recycler2 = recycler;
                state2 = state;
                z = false;
            } else {
                i2 = -1;
            }
            if (z4 || layoutState2.mLayoutDirection != 1) {
                int i14;
                i10 = i5;
                if (layoutState2.mLayoutDirection == -1) {
                    i3 = layoutState2.mOffset - layoutChunkResult2.mConsumed;
                    i = i3 - i10;
                    i8 = getWidth();
                    i7 = i3;
                    i14 = i;
                } else {
                    i3 = layoutState2.mOffset + layoutChunkResult2.mConsumed;
                    i14 = i3;
                    i7 = i3 + i10;
                    i8 = 0;
                }
                i3 = i8;
                i8 = 0;
                while (i8 < i11) {
                    View view3 = r6.mSet[i8];
                    layoutParams3 = (GridLayoutManager.LayoutParams) view3.getLayoutParams();
                    i = r6.mOrientationHelper.getDecoratedMeasurementInOther(view3);
                    if (layoutState2.mLayoutDirection == -1) {
                        i3 -= i;
                    }
                    i6 = i3;
                    i12 = i6 + i;
                    decoratedMeasurement = i14;
                    int i15 = i14;
                    layoutParams = layoutParams3;
                    View view4 = view3;
                    layoutDecoratedWithMargins(view3, i6, decoratedMeasurement, i12, i7);
                    i3 = layoutState2.mLayoutDirection != -1 ? i12 : i6;
                    if (!layoutParams.isItemRemoved()) {
                        if (!layoutParams.isItemChanged()) {
                            layoutChunkResult2.mFocusable |= view4.hasFocusable();
                            i8++;
                            i14 = i15;
                            state2 = state;
                        }
                    }
                    layoutChunkResult2.mIgnoreConsumed = true;
                    layoutChunkResult2.mFocusable |= view4.hasFocusable();
                    i8++;
                    i14 = i15;
                    state2 = state;
                }
                layoutChunkResult2.mConsumed += i10;
                Arrays.fill(r6.mSet, null);
                i11 = -1;
                z2 = true;
                z4 = z5;
                recycler2 = recycler;
                state2 = state;
                z = false;
            }
            if (layoutState2.mLayoutDirection != i2) {
                i3 = layoutState2.mOffset + layoutChunkResult2.mConsumed;
                i = i3 + i5;
                i8 = getWidth();
                i7 = i3;
                i6 = i;
            } else {
                i3 = layoutState2.mOffset - layoutChunkResult2.mConsumed;
                i6 = i3;
                i7 = i3 - i5;
                i8 = z6;
            }
            i9 = i8;
            i8 = i11 - 1;
            i3 = i9;
            while (i8 >= 0) {
                View view22 = r6.mSet[i8];
                GridLayoutManager.LayoutParams layoutParams32 = (GridLayoutManager.LayoutParams) view22.getLayoutParams();
                i = r6.mOrientationHelper.getDecoratedMeasurementInOther(view22);
                if (layoutState2.mLayoutDirection == 1) {
                    i3 -= i;
                }
                int i122 = i3;
                int i132 = i122 + i;
                layoutParams = layoutParams32;
                i10 = i5;
                layoutDecoratedWithMargins(view22, i122, i7, i132, i6);
                if (layoutState2.mLayoutDirection != -1) {
                }
                if (layoutParams.isItemRemoved()) {
                }
                layoutChunkResult2.mIgnoreConsumed = true;
                layoutChunkResult2.mFocusable |= view22.hasFocusable();
                i8--;
                i5 = i10;
            }
            i10 = i5;
            layoutChunkResult2.mConsumed += i10;
            Arrays.fill(r6.mSet, null);
            i11 = -1;
            z2 = true;
            z4 = z5;
            recycler2 = recycler;
            state2 = state;
            z = false;
        }
    }
}
