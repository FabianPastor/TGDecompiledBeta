package org.telegram.messenger.support.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.MarginLayoutParams;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.CollectionItemInfoCompat;
import java.util.Arrays;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager.LayoutPrefetchRegistry;
import org.telegram.messenger.support.widget.RecyclerView.Recycler;
import org.telegram.messenger.support.widget.RecyclerView.State;

public class GridLayoutManager extends LinearLayoutManager {
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

    public static abstract class SpanSizeLookup {
        private boolean mCacheSpanIndices = false;
        final SparseIntArray mSpanIndexCache = new SparseIntArray();

        public abstract int getSpanSize(int i);

        public void setSpanIndexCacheEnabled(boolean z) {
            this.mCacheSpanIndices = z;
        }

        public void invalidateSpanIndexCache() {
            this.mSpanIndexCache.clear();
        }

        public boolean isSpanIndexCacheEnabled() {
            return this.mCacheSpanIndices;
        }

        /* Access modifiers changed, original: 0000 */
        public int getCachedSpanIndex(int i, int i2) {
            if (!this.mCacheSpanIndices) {
                return getSpanIndex(i, i2);
            }
            int i3 = this.mSpanIndexCache.get(i, -1);
            if (i3 != -1) {
                return i3;
            }
            i2 = getSpanIndex(i, i2);
            this.mSpanIndexCache.put(i, i2);
            return i2;
        }

        /* JADX WARNING: Removed duplicated region for block: B:12:0x002a  */
        /* JADX WARNING: Removed duplicated region for block: B:12:0x002a  */
        /* JADX WARNING: Removed duplicated region for block: B:21:0x003d A:{RETURN} */
        /* JADX WARNING: Removed duplicated region for block: B:20:0x003c A:{RETURN} */
        public int getSpanIndex(int r6, int r7) {
            /*
            r5 = this;
            r0 = r5.getSpanSize(r6);
            r1 = 0;
            if (r0 != r7) goto L_0x0008;
        L_0x0007:
            return r1;
        L_0x0008:
            r2 = r5.mCacheSpanIndices;
            if (r2 == 0) goto L_0x0026;
        L_0x000c:
            r2 = r5.mSpanIndexCache;
            r2 = r2.size();
            if (r2 <= 0) goto L_0x0026;
        L_0x0014:
            r2 = r5.findReferenceIndexFromCache(r6);
            if (r2 < 0) goto L_0x0026;
        L_0x001a:
            r3 = r5.mSpanIndexCache;
            r3 = r3.get(r2);
            r4 = r5.getSpanSize(r2);
            r3 = r3 + r4;
            goto L_0x0036;
        L_0x0026:
            r2 = 0;
            r3 = 0;
        L_0x0028:
            if (r2 >= r6) goto L_0x0039;
        L_0x002a:
            r4 = r5.getSpanSize(r2);
            r3 = r3 + r4;
            if (r3 != r7) goto L_0x0033;
        L_0x0031:
            r3 = 0;
            goto L_0x0036;
        L_0x0033:
            if (r3 <= r7) goto L_0x0036;
        L_0x0035:
            r3 = r4;
        L_0x0036:
            r2 = r2 + 1;
            goto L_0x0028;
        L_0x0039:
            r0 = r0 + r3;
            if (r0 > r7) goto L_0x003d;
        L_0x003c:
            return r3;
        L_0x003d:
            return r1;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.support.widget.GridLayoutManager$SpanSizeLookup.getSpanIndex(int, int):int");
        }

        /* Access modifiers changed, original: 0000 */
        public int findReferenceIndexFromCache(int i) {
            int size = this.mSpanIndexCache.size() - 1;
            int i2 = 0;
            while (i2 <= size) {
                int i3 = (i2 + size) >>> 1;
                if (this.mSpanIndexCache.keyAt(i3) < i) {
                    i2 = i3 + 1;
                } else {
                    size = i3 - 1;
                }
            }
            i2--;
            return (i2 < 0 || i2 >= this.mSpanIndexCache.size()) ? -1 : this.mSpanIndexCache.keyAt(i2);
        }

        public int getSpanGroupIndex(int i, int i2) {
            int spanSize = getSpanSize(i);
            int i3 = 0;
            int i4 = 0;
            for (int i5 = 0; i5 < i; i5++) {
                int spanSize2 = getSpanSize(i5);
                i3 += spanSize2;
                if (i3 == i2) {
                    i4++;
                    i3 = 0;
                } else if (i3 > i2) {
                    i4++;
                    i3 = spanSize2;
                }
            }
            return i3 + spanSize > i2 ? i4 + 1 : i4;
        }
    }

    public static final class DefaultSpanSizeLookup extends SpanSizeLookup {
        public int getSpanSize(int i) {
            return 1;
        }

        public int getSpanIndex(int i, int i2) {
            return i % i2;
        }
    }

    public static class LayoutParams extends org.telegram.messenger.support.widget.RecyclerView.LayoutParams {
        public static final int INVALID_SPAN_ID = -1;
        int mSpanIndex = -1;
        int mSpanSize = 0;

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        public LayoutParams(int i, int i2) {
            super(i, i2);
        }

        public LayoutParams(MarginLayoutParams marginLayoutParams) {
            super(marginLayoutParams);
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
        }

        public LayoutParams(org.telegram.messenger.support.widget.RecyclerView.LayoutParams layoutParams) {
            super(layoutParams);
        }

        public int getSpanIndex() {
            return this.mSpanIndex;
        }

        public int getSpanSize() {
            return this.mSpanSize;
        }
    }

    public GridLayoutManager(Context context, int i) {
        super(context);
        setSpanCount(i);
    }

    public GridLayoutManager(Context context, int i, int i2, boolean z) {
        super(context, i2, z);
        setSpanCount(i);
    }

    public void setStackFromEnd(boolean z) {
        if (z) {
            throw new UnsupportedOperationException("GridLayoutManager does not support stack from end. Consider using reverse layout");
        }
        super.setStackFromEnd(false);
    }

    public int getRowCountForAccessibility(Recycler recycler, State state) {
        if (this.mOrientation == 0) {
            return this.mSpanCount;
        }
        if (state.getItemCount() < 1) {
            return 0;
        }
        return getSpanGroupIndex(recycler, state, state.getItemCount() - 1) + 1;
    }

    public int getColumnCountForAccessibility(Recycler recycler, State state) {
        if (this.mOrientation == 1) {
            return this.mSpanCount;
        }
        if (state.getItemCount() < 1) {
            return 0;
        }
        return getSpanGroupIndex(recycler, state, state.getItemCount() - 1) + 1;
    }

    public void onInitializeAccessibilityNodeInfoForItem(Recycler recycler, State state, View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
        android.view.ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams instanceof LayoutParams) {
            LayoutParams layoutParams2 = (LayoutParams) layoutParams;
            int spanGroupIndex = getSpanGroupIndex(recycler, state, layoutParams2.getViewLayoutPosition());
            boolean z;
            if (this.mOrientation == 0) {
                int spanIndex = layoutParams2.getSpanIndex();
                int spanSize = layoutParams2.getSpanSize();
                z = this.mSpanCount > 1 && layoutParams2.getSpanSize() == this.mSpanCount;
                accessibilityNodeInfoCompat.setCollectionItemInfo(CollectionItemInfoCompat.obtain(spanIndex, spanSize, spanGroupIndex, 1, z, false));
            } else {
                int spanIndex2 = layoutParams2.getSpanIndex();
                int spanSize2 = layoutParams2.getSpanSize();
                z = this.mSpanCount > 1 && layoutParams2.getSpanSize() == this.mSpanCount;
                accessibilityNodeInfoCompat.setCollectionItemInfo(CollectionItemInfoCompat.obtain(spanGroupIndex, 1, spanIndex2, spanSize2, z, false));
            }
            return;
        }
        super.onInitializeAccessibilityNodeInfoForItem(view, accessibilityNodeInfoCompat);
    }

    public void onLayoutChildren(Recycler recycler, State state) {
        if (state.isPreLayout()) {
            cachePreLayoutSpanMapping();
        }
        super.onLayoutChildren(recycler, state);
        clearPreLayoutSpanMappingCache();
    }

    public void onLayoutCompleted(State state) {
        super.onLayoutCompleted(state);
        this.mPendingSpanCountChange = false;
    }

    private void clearPreLayoutSpanMappingCache() {
        this.mPreLayoutSpanSizeCache.clear();
        this.mPreLayoutSpanIndexCache.clear();
    }

    private void cachePreLayoutSpanMapping() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            LayoutParams layoutParams = (LayoutParams) getChildAt(i).getLayoutParams();
            int viewLayoutPosition = layoutParams.getViewLayoutPosition();
            this.mPreLayoutSpanSizeCache.put(viewLayoutPosition, layoutParams.getSpanSize());
            this.mPreLayoutSpanIndexCache.put(viewLayoutPosition, layoutParams.getSpanIndex());
        }
    }

    public void onItemsAdded(RecyclerView recyclerView, int i, int i2) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
    }

    public void onItemsChanged(RecyclerView recyclerView) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
    }

    public void onItemsRemoved(RecyclerView recyclerView, int i, int i2) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
    }

    public void onItemsUpdated(RecyclerView recyclerView, int i, int i2, Object obj) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
    }

    public void onItemsMoved(RecyclerView recyclerView, int i, int i2, int i3) {
        this.mSpanSizeLookup.invalidateSpanIndexCache();
    }

    public org.telegram.messenger.support.widget.RecyclerView.LayoutParams generateDefaultLayoutParams() {
        if (this.mOrientation == 0) {
            return new LayoutParams(-2, -1);
        }
        return new LayoutParams(-1, -2);
    }

    public org.telegram.messenger.support.widget.RecyclerView.LayoutParams generateLayoutParams(Context context, AttributeSet attributeSet) {
        return new LayoutParams(context, attributeSet);
    }

    public org.telegram.messenger.support.widget.RecyclerView.LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
        if (layoutParams instanceof MarginLayoutParams) {
            return new LayoutParams((MarginLayoutParams) layoutParams);
        }
        return new LayoutParams(layoutParams);
    }

    public boolean checkLayoutParams(org.telegram.messenger.support.widget.RecyclerView.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }

    public void setSpanSizeLookup(SpanSizeLookup spanSizeLookup) {
        this.mSpanSizeLookup = spanSizeLookup;
    }

    public SpanSizeLookup getSpanSizeLookup() {
        return this.mSpanSizeLookup;
    }

    private void updateMeasurements() {
        int width;
        int paddingLeft;
        if (getOrientation() == 1) {
            width = getWidth() - getPaddingRight();
            paddingLeft = getPaddingLeft();
        } else {
            width = getHeight() - getPaddingBottom();
            paddingLeft = getPaddingTop();
        }
        calculateItemBorders(width - paddingLeft);
    }

    public void setMeasuredDimension(Rect rect, int i, int i2) {
        int chooseSize;
        if (this.mCachedBorders == null) {
            super.setMeasuredDimension(rect, i, i2);
        }
        int paddingLeft = getPaddingLeft() + getPaddingRight();
        int paddingTop = getPaddingTop() + getPaddingBottom();
        if (this.mOrientation == 1) {
            chooseSize = LayoutManager.chooseSize(i2, rect.height() + paddingTop, getMinimumHeight());
            int[] iArr = this.mCachedBorders;
            i = LayoutManager.chooseSize(i, iArr[iArr.length - 1] + paddingLeft, getMinimumWidth());
        } else {
            i = LayoutManager.chooseSize(i, rect.width() + paddingLeft, getMinimumWidth());
            int[] iArr2 = this.mCachedBorders;
            chooseSize = LayoutManager.chooseSize(i2, iArr2[iArr2.length - 1] + paddingTop, getMinimumHeight());
        }
        setMeasuredDimension(i, chooseSize);
    }

    /* Access modifiers changed, original: protected */
    public void calculateItemBorders(int i) {
        this.mCachedBorders = calculateItemBorders(this.mCachedBorders, this.mSpanCount, i);
    }

    /* Access modifiers changed, original: protected */
    public int[] calculateItemBorders(int[] iArr, int i, int i2) {
        int i3 = 1;
        if (!(iArr != null && iArr.length == i + 1 && iArr[iArr.length - 1] == i2)) {
            iArr = new int[(i + 1)];
        }
        int i4 = 0;
        iArr[0] = 0;
        int i5 = i2 / i;
        i2 %= i;
        int i6 = 0;
        while (i3 <= i) {
            int i7;
            i4 += i2;
            if (i4 <= 0 || i - i4 >= i2) {
                i7 = i5;
            } else {
                i7 = i5 + 1;
                i4 -= i;
            }
            i6 += i7;
            iArr[i3] = i6;
            i3++;
        }
        return iArr;
    }

    /* Access modifiers changed, original: 0000 */
    public int getSpaceForSpanRange(int i, int i2) {
        int[] iArr;
        if (this.mOrientation == 1 && isLayoutRTL()) {
            iArr = this.mCachedBorders;
            int i3 = this.mSpanCount;
            return iArr[i3 - i] - iArr[(i3 - i) - i2];
        }
        iArr = this.mCachedBorders;
        return iArr[i2 + i] - iArr[i];
    }

    /* Access modifiers changed, original: 0000 */
    public void onAnchorReady(Recycler recycler, State state, AnchorInfo anchorInfo, int i) {
        super.onAnchorReady(recycler, state, anchorInfo, i);
        updateMeasurements();
        if (state.getItemCount() > 0 && !state.isPreLayout()) {
            ensureAnchorIsInCorrectSpan(recycler, state, anchorInfo, i);
        }
        ensureViewSet();
    }

    private void ensureViewSet() {
        View[] viewArr = this.mSet;
        if (viewArr == null || viewArr.length != this.mSpanCount) {
            this.mSet = new View[this.mSpanCount];
        }
    }

    public int scrollHorizontallyBy(int i, Recycler recycler, State state) {
        updateMeasurements();
        ensureViewSet();
        return super.scrollHorizontallyBy(i, recycler, state);
    }

    public int scrollVerticallyBy(int i, Recycler recycler, State state) {
        updateMeasurements();
        ensureViewSet();
        return super.scrollVerticallyBy(i, recycler, state);
    }

    private void ensureAnchorIsInCorrectSpan(Recycler recycler, State state, AnchorInfo anchorInfo, int i) {
        Object obj = i == 1 ? 1 : null;
        int spanIndex = getSpanIndex(recycler, state, anchorInfo.mPosition);
        if (obj != null) {
            while (spanIndex > 0) {
                i = anchorInfo.mPosition;
                if (i > 0) {
                    anchorInfo.mPosition = i - 1;
                    spanIndex = getSpanIndex(recycler, state, anchorInfo.mPosition);
                } else {
                    return;
                }
            }
            return;
        }
        i = state.getItemCount() - 1;
        int i2 = anchorInfo.mPosition;
        while (i2 < i) {
            int i3 = i2 + 1;
            int spanIndex2 = getSpanIndex(recycler, state, i3);
            if (spanIndex2 <= spanIndex) {
                break;
            }
            i2 = i3;
            spanIndex = spanIndex2;
        }
        anchorInfo.mPosition = i2;
    }

    /* Access modifiers changed, original: 0000 */
    public View findReferenceChild(Recycler recycler, State state, int i, int i2, int i3) {
        ensureLayoutState();
        int startAfterPadding = this.mOrientationHelper.getStartAfterPadding();
        int endAfterPadding = this.mOrientationHelper.getEndAfterPadding();
        int i4 = i2 > i ? 1 : -1;
        View view = null;
        View view2 = null;
        while (i != i2) {
            View childAt = getChildAt(i);
            int position = getPosition(childAt);
            if (position >= 0 && position < i3 && getSpanIndex(recycler, state, position) == 0) {
                if (((org.telegram.messenger.support.widget.RecyclerView.LayoutParams) childAt.getLayoutParams()).isItemRemoved()) {
                    if (view2 == null) {
                        view2 = childAt;
                    }
                } else if (this.mOrientationHelper.getDecoratedStart(childAt) < endAfterPadding && this.mOrientationHelper.getDecoratedEnd(childAt) >= startAfterPadding) {
                    return childAt;
                } else {
                    if (view == null) {
                        view = childAt;
                    }
                }
            }
            i += i4;
        }
        if (view == null) {
            view = view2;
        }
        return view;
    }

    private int getSpanGroupIndex(Recycler recycler, State state, int i) {
        if (!state.isPreLayout()) {
            return this.mSpanSizeLookup.getSpanGroupIndex(i, this.mSpanCount);
        }
        int convertPreLayoutPositionToPostLayout = recycler.convertPreLayoutPositionToPostLayout(i);
        if (convertPreLayoutPositionToPostLayout != -1) {
            return this.mSpanSizeLookup.getSpanGroupIndex(convertPreLayoutPositionToPostLayout, this.mSpanCount);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Cannot find span size for pre layout position. ");
        stringBuilder.append(i);
        Log.w("GridLayoutManager", stringBuilder.toString());
        return 0;
    }

    private int getSpanIndex(Recycler recycler, State state, int i) {
        if (!state.isPreLayout()) {
            return this.mSpanSizeLookup.getCachedSpanIndex(i, this.mSpanCount);
        }
        int i2 = this.mPreLayoutSpanIndexCache.get(i, -1);
        if (i2 != -1) {
            return i2;
        }
        int convertPreLayoutPositionToPostLayout = recycler.convertPreLayoutPositionToPostLayout(i);
        if (convertPreLayoutPositionToPostLayout != -1) {
            return this.mSpanSizeLookup.getCachedSpanIndex(convertPreLayoutPositionToPostLayout, this.mSpanCount);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Cannot find span size for pre layout position. It is not cached, not in the adapter. Pos:");
        stringBuilder.append(i);
        Log.w("GridLayoutManager", stringBuilder.toString());
        return 0;
    }

    /* Access modifiers changed, original: protected */
    public int getSpanSize(Recycler recycler, State state, int i) {
        if (!state.isPreLayout()) {
            return this.mSpanSizeLookup.getSpanSize(i);
        }
        int i2 = this.mPreLayoutSpanSizeCache.get(i, -1);
        if (i2 != -1) {
            return i2;
        }
        int convertPreLayoutPositionToPostLayout = recycler.convertPreLayoutPositionToPostLayout(i);
        if (convertPreLayoutPositionToPostLayout != -1) {
            return this.mSpanSizeLookup.getSpanSize(convertPreLayoutPositionToPostLayout);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Cannot find span size for pre layout position. It is not cached, not in the adapter. Pos:");
        stringBuilder.append(i);
        Log.w("GridLayoutManager", stringBuilder.toString());
        return 1;
    }

    /* Access modifiers changed, original: 0000 */
    public void collectPrefetchPositionsForLayoutState(State state, LayoutState layoutState, LayoutPrefetchRegistry layoutPrefetchRegistry) {
        int i = this.mSpanCount;
        for (int i2 = 0; i2 < this.mSpanCount && layoutState.hasMore(state) && i > 0; i2++) {
            int i3 = layoutState.mCurrentPosition;
            layoutPrefetchRegistry.addPosition(i3, Math.max(0, layoutState.mScrollingOffset));
            i -= this.mSpanSizeLookup.getSpanSize(i3);
            layoutState.mCurrentPosition += layoutState.mItemDirection;
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void layoutChunk(Recycler recycler, State state, LayoutState layoutState, LayoutChunkResult layoutChunkResult) {
        int i;
        int spanSize;
        Recycler recycler2 = recycler;
        State state2 = state;
        LayoutState layoutState2 = layoutState;
        LayoutChunkResult layoutChunkResult2 = layoutChunkResult;
        int modeInOther = this.mOrientationHelper.getModeInOther();
        Object obj = modeInOther != NUM ? 1 : null;
        int i2 = getChildCount() > 0 ? this.mCachedBorders[this.mSpanCount] : 0;
        if (obj != null) {
            updateMeasurements();
        }
        boolean z = layoutState2.mItemDirection == 1;
        int i3 = this.mSpanCount;
        if (!z) {
            i3 = getSpanIndex(recycler2, state2, layoutState2.mCurrentPosition) + getSpanSize(recycler2, state2, layoutState2.mCurrentPosition);
        }
        int i4 = 0;
        int i5 = 0;
        while (i5 < this.mSpanCount && layoutState2.hasMore(state2) && i3 > 0) {
            i = layoutState2.mCurrentPosition;
            spanSize = getSpanSize(recycler2, state2, i);
            if (spanSize <= this.mSpanCount) {
                i3 -= spanSize;
                if (i3 < 0) {
                    break;
                }
                View next = layoutState2.next(recycler2);
                if (next == null) {
                    break;
                }
                i4 += spanSize;
                this.mSet[i5] = next;
                i5++;
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Item at position ");
                stringBuilder.append(i);
                stringBuilder.append(" requires ");
                stringBuilder.append(spanSize);
                stringBuilder.append(" spans but GridLayoutManager has only ");
                stringBuilder.append(this.mSpanCount);
                stringBuilder.append(" spans.");
                throw new IllegalArgumentException(stringBuilder.toString());
            }
        }
        if (i5 == 0) {
            layoutChunkResult2.mFinished = true;
            return;
        }
        View view;
        int decoratedMeasurement;
        float f = 0.0f;
        int i6 = i5;
        assignSpans(recycler, state, i5, i4, z);
        int i7 = 0;
        for (i3 = 0; i3 < i6; i3++) {
            boolean z2;
            view = this.mSet[i3];
            if (layoutState2.mScrapList != null) {
                z2 = false;
                if (z) {
                    addDisappearingView(view);
                } else {
                    addDisappearingView(view, 0);
                }
            } else if (z) {
                addView(view);
                z2 = false;
            } else {
                z2 = false;
                addView(view, 0);
            }
            calculateItemDecorationsForChild(view, this.mDecorInsets);
            measureChild(view, modeInOther, z2);
            i = this.mOrientationHelper.getDecoratedMeasurement(view);
            if (i > i7) {
                i7 = i;
            }
            float decoratedMeasurementInOther = (((float) this.mOrientationHelper.getDecoratedMeasurementInOther(view)) * 1.0f) / ((float) ((LayoutParams) view.getLayoutParams()).mSpanSize);
            if (decoratedMeasurementInOther > f) {
                f = decoratedMeasurementInOther;
            }
        }
        if (obj != null) {
            guessMeasurement(f, i2);
            i7 = 0;
            for (i3 = 0; i3 < i6; i3++) {
                view = this.mSet[i3];
                measureChild(view, NUM, true);
                decoratedMeasurement = this.mOrientationHelper.getDecoratedMeasurement(view);
                if (decoratedMeasurement > i7) {
                    i7 = decoratedMeasurement;
                }
            }
        }
        for (i3 = 0; i3 < i6; i3++) {
            view = this.mSet[i3];
            if (this.mOrientationHelper.getDecoratedMeasurement(view) != i7) {
                LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
                Rect rect = layoutParams.mDecorInsets;
                i5 = ((rect.top + rect.bottom) + layoutParams.topMargin) + layoutParams.bottomMargin;
                modeInOther = ((rect.left + rect.right) + layoutParams.leftMargin) + layoutParams.rightMargin;
                i4 = getSpaceForSpanRange(layoutParams.mSpanIndex, layoutParams.mSpanSize);
                if (this.mOrientation == 1) {
                    i = LayoutManager.getChildMeasureSpec(i4, NUM, modeInOther, layoutParams.width, false);
                    i4 = MeasureSpec.makeMeasureSpec(i7 - i5, NUM);
                } else {
                    modeInOther = MeasureSpec.makeMeasureSpec(i7 - modeInOther, NUM);
                    i4 = LayoutManager.getChildMeasureSpec(i4, NUM, i5, layoutParams.height, false);
                    i = modeInOther;
                }
                measureChildWithDecorationsAndMargin(view, i, i4, true);
            }
        }
        int i8 = 0;
        layoutChunkResult2.mConsumed = i7;
        if (this.mOrientation == 1) {
            if (layoutState2.mLayoutDirection == -1) {
                i3 = layoutState2.mOffset;
                i = i3;
                decoratedMeasurement = i3 - i7;
            } else {
                i3 = layoutState2.mOffset;
                decoratedMeasurement = i3;
                i = i7 + i3;
            }
            i3 = 0;
            i7 = 0;
        } else if (layoutState2.mLayoutDirection == -1) {
            i3 = layoutState2.mOffset;
            decoratedMeasurement = 0;
            i = 0;
            int i9 = i3 - i7;
            i7 = i3;
            i3 = i9;
        } else {
            i3 = layoutState2.mOffset;
            i7 += i3;
            decoratedMeasurement = 0;
            i = 0;
        }
        while (i8 < i6) {
            int i10;
            int i11;
            View view2 = this.mSet[i8];
            LayoutParams layoutParams2 = (LayoutParams) view2.getLayoutParams();
            if (this.mOrientation != 1) {
                decoratedMeasurement = getPaddingTop() + this.mCachedBorders[layoutParams2.mSpanIndex];
                i = this.mOrientationHelper.getDecoratedMeasurementInOther(view2) + decoratedMeasurement;
            } else if (isLayoutRTL()) {
                i3 = getPaddingLeft() + this.mCachedBorders[this.mSpanCount - layoutParams2.mSpanIndex];
                i10 = i3;
                spanSize = i3 - this.mOrientationHelper.getDecoratedMeasurementInOther(view2);
                i2 = decoratedMeasurement;
                i11 = i;
                layoutDecoratedWithMargins(view2, spanSize, i2, i10, i11);
                if (!layoutParams2.isItemRemoved() || layoutParams2.isItemChanged()) {
                    layoutChunkResult2.mIgnoreConsumed = true;
                }
                layoutChunkResult2.mFocusable |= view2.hasFocusable();
                i8++;
                i3 = spanSize;
                decoratedMeasurement = i2;
                i7 = i10;
                i = i11;
            } else {
                i3 = getPaddingLeft() + this.mCachedBorders[layoutParams2.mSpanIndex];
                i7 = this.mOrientationHelper.getDecoratedMeasurementInOther(view2) + i3;
            }
            spanSize = i3;
            i10 = i7;
            i2 = decoratedMeasurement;
            i11 = i;
            layoutDecoratedWithMargins(view2, spanSize, i2, i10, i11);
            if (layoutParams2.isItemRemoved()) {
            }
            layoutChunkResult2.mIgnoreConsumed = true;
            layoutChunkResult2.mFocusable |= view2.hasFocusable();
            i8++;
            i3 = spanSize;
            decoratedMeasurement = i2;
            i7 = i10;
            i = i11;
        }
        Arrays.fill(this.mSet, null);
    }

    /* Access modifiers changed, original: protected */
    public void measureChild(View view, int i, boolean z) {
        int childMeasureSpec;
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        Rect rect = layoutParams.mDecorInsets;
        int i2 = ((rect.top + rect.bottom) + layoutParams.topMargin) + layoutParams.bottomMargin;
        int i3 = ((rect.left + rect.right) + layoutParams.leftMargin) + layoutParams.rightMargin;
        int spaceForSpanRange = getSpaceForSpanRange(layoutParams.mSpanIndex, layoutParams.mSpanSize);
        if (this.mOrientation == 1) {
            i = LayoutManager.getChildMeasureSpec(spaceForSpanRange, i, i3, layoutParams.width, false);
            childMeasureSpec = LayoutManager.getChildMeasureSpec(this.mOrientationHelper.getTotalSpace(), getHeightMode(), i2, layoutParams.height, true);
        } else {
            i = LayoutManager.getChildMeasureSpec(spaceForSpanRange, i, i2, layoutParams.height, false);
            int childMeasureSpec2 = LayoutManager.getChildMeasureSpec(this.mOrientationHelper.getTotalSpace(), getWidthMode(), i3, layoutParams.width, true);
            childMeasureSpec = i;
            i = childMeasureSpec2;
        }
        measureChildWithDecorationsAndMargin(view, i, childMeasureSpec, z);
    }

    private void guessMeasurement(float f, int i) {
        calculateItemBorders(Math.max(Math.round(f * ((float) this.mSpanCount)), i));
    }

    /* Access modifiers changed, original: protected */
    public void measureChildWithDecorationsAndMargin(View view, int i, int i2, boolean z) {
        org.telegram.messenger.support.widget.RecyclerView.LayoutParams layoutParams = (org.telegram.messenger.support.widget.RecyclerView.LayoutParams) view.getLayoutParams();
        if (z) {
            z = shouldReMeasureChild(view, i, i2, layoutParams);
        } else {
            z = shouldMeasureChild(view, i, i2, layoutParams);
        }
        if (z) {
            view.measure(i, i2);
        }
    }

    /* Access modifiers changed, original: protected */
    public void assignSpans(Recycler recycler, State state, int i, int i2, boolean z) {
        int i3;
        i2 = 0;
        int i4 = -1;
        if (z) {
            i4 = i;
            i = 0;
            i3 = 1;
        } else {
            i--;
            i3 = -1;
        }
        while (i != i4) {
            View view = this.mSet[i];
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            layoutParams.mSpanSize = getSpanSize(recycler, state, getPosition(view));
            layoutParams.mSpanIndex = i2;
            i2 += layoutParams.mSpanSize;
            i += i3;
        }
    }

    public int getSpanCount() {
        return this.mSpanCount;
    }

    public void setSpanCount(int i) {
        if (i != this.mSpanCount) {
            this.mPendingSpanCountChange = true;
            if (i >= 1) {
                this.mSpanCount = i;
                this.mSpanSizeLookup.invalidateSpanIndexCache();
                requestLayout();
                return;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Span count should be at least 1. Provided ");
            stringBuilder.append(i);
            throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:72:0x0105  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x0105  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x0105  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x0105  */
    /* JADX WARNING: Missing block: B:55:0x00d7, code skipped:
            if (r13 == (r2 > r8)) goto L_0x00cd;
     */
    /* JADX WARNING: Missing block: B:66:0x00f7, code skipped:
            if (r13 == r11) goto L_0x00b7;
     */
    public android.view.View onFocusSearchFailed(android.view.View r24, int r25, org.telegram.messenger.support.widget.RecyclerView.Recycler r26, org.telegram.messenger.support.widget.RecyclerView.State r27) {
        /*
        r23 = this;
        r0 = r23;
        r1 = r26;
        r2 = r27;
        r3 = r23.findContainingItemView(r24);
        r4 = 0;
        if (r3 != 0) goto L_0x000e;
    L_0x000d:
        return r4;
    L_0x000e:
        r5 = r3.getLayoutParams();
        r5 = (org.telegram.messenger.support.widget.GridLayoutManager.LayoutParams) r5;
        r6 = r5.mSpanIndex;
        r5 = r5.mSpanSize;
        r5 = r5 + r6;
        r7 = super.onFocusSearchFailed(r24, r25, r26, r27);
        if (r7 != 0) goto L_0x0020;
    L_0x001f:
        return r4;
    L_0x0020:
        r7 = r25;
        r7 = r0.convertFocusDirectionToLayoutDirection(r7);
        r9 = 1;
        if (r7 != r9) goto L_0x002b;
    L_0x0029:
        r7 = 1;
        goto L_0x002c;
    L_0x002b:
        r7 = 0;
    L_0x002c:
        r10 = r0.mShouldReverseLayout;
        if (r7 == r10) goto L_0x0032;
    L_0x0030:
        r7 = 1;
        goto L_0x0033;
    L_0x0032:
        r7 = 0;
    L_0x0033:
        r10 = -1;
        if (r7 == 0) goto L_0x003e;
    L_0x0036:
        r7 = r23.getChildCount();
        r7 = r7 - r9;
        r11 = -1;
        r12 = -1;
        goto L_0x0045;
    L_0x003e:
        r7 = r23.getChildCount();
        r11 = r7;
        r7 = 0;
        r12 = 1;
    L_0x0045:
        r13 = r0.mOrientation;
        if (r13 != r9) goto L_0x0051;
    L_0x0049:
        r13 = r23.isLayoutRTL();
        if (r13 == 0) goto L_0x0051;
    L_0x004f:
        r13 = 1;
        goto L_0x0052;
    L_0x0051:
        r13 = 0;
    L_0x0052:
        r14 = r0.getSpanGroupIndex(r1, r2, r7);
        r10 = r4;
        r8 = -1;
        r15 = 0;
        r16 = 0;
        r17 = -1;
    L_0x005d:
        if (r7 == r11) goto L_0x0147;
    L_0x005f:
        r9 = r0.getSpanGroupIndex(r1, r2, r7);
        r1 = r0.getChildAt(r7);
        if (r1 != r3) goto L_0x006b;
    L_0x0069:
        goto L_0x0147;
    L_0x006b:
        r18 = r1.hasFocusable();
        if (r18 == 0) goto L_0x0085;
    L_0x0071:
        if (r9 == r14) goto L_0x0085;
    L_0x0073:
        if (r4 == 0) goto L_0x0077;
    L_0x0075:
        goto L_0x0147;
    L_0x0077:
        r18 = r3;
        r19 = r8;
        r21 = r10;
        r20 = r11;
        r8 = r16;
        r10 = r17;
        goto L_0x0133;
    L_0x0085:
        r9 = r1.getLayoutParams();
        r9 = (org.telegram.messenger.support.widget.GridLayoutManager.LayoutParams) r9;
        r2 = r9.mSpanIndex;
        r18 = r3;
        r3 = r9.mSpanSize;
        r3 = r3 + r2;
        r19 = r1.hasFocusable();
        if (r19 == 0) goto L_0x009d;
    L_0x0098:
        if (r2 != r6) goto L_0x009d;
    L_0x009a:
        if (r3 != r5) goto L_0x009d;
    L_0x009c:
        return r1;
    L_0x009d:
        r19 = r1.hasFocusable();
        if (r19 == 0) goto L_0x00a5;
    L_0x00a3:
        if (r4 == 0) goto L_0x00ad;
    L_0x00a5:
        r19 = r1.hasFocusable();
        if (r19 != 0) goto L_0x00b9;
    L_0x00ab:
        if (r10 != 0) goto L_0x00b9;
    L_0x00ad:
        r19 = r8;
        r21 = r10;
    L_0x00b1:
        r20 = r11;
        r8 = r16;
        r10 = r17;
    L_0x00b7:
        r11 = 1;
        goto L_0x0103;
    L_0x00b9:
        r19 = java.lang.Math.max(r2, r6);
        r20 = java.lang.Math.min(r3, r5);
        r21 = r10;
        r10 = r20 - r19;
        r19 = r1.hasFocusable();
        if (r19 == 0) goto L_0x00da;
    L_0x00cb:
        if (r10 <= r15) goto L_0x00d0;
    L_0x00cd:
        r19 = r8;
        goto L_0x00b1;
    L_0x00d0:
        if (r10 != r15) goto L_0x00fa;
    L_0x00d2:
        if (r2 <= r8) goto L_0x00d6;
    L_0x00d4:
        r10 = 1;
        goto L_0x00d7;
    L_0x00d6:
        r10 = 0;
    L_0x00d7:
        if (r13 != r10) goto L_0x00fa;
    L_0x00d9:
        goto L_0x00cd;
    L_0x00da:
        if (r4 != 0) goto L_0x00fa;
    L_0x00dc:
        r19 = r8;
        r20 = r11;
        r8 = 0;
        r11 = 1;
        r22 = r0.isViewPartiallyVisible(r1, r8, r11);
        if (r22 == 0) goto L_0x00fe;
    L_0x00e8:
        r8 = r16;
        if (r10 <= r8) goto L_0x00ef;
    L_0x00ec:
        r10 = r17;
        goto L_0x0103;
    L_0x00ef:
        if (r10 != r8) goto L_0x0100;
    L_0x00f1:
        r10 = r17;
        if (r2 <= r10) goto L_0x00f6;
    L_0x00f5:
        goto L_0x00f7;
    L_0x00f6:
        r11 = 0;
    L_0x00f7:
        if (r13 != r11) goto L_0x0102;
    L_0x00f9:
        goto L_0x00b7;
    L_0x00fa:
        r19 = r8;
        r20 = r11;
    L_0x00fe:
        r8 = r16;
    L_0x0100:
        r10 = r17;
    L_0x0102:
        r11 = 0;
    L_0x0103:
        if (r11 == 0) goto L_0x0133;
    L_0x0105:
        r11 = r1.hasFocusable();
        if (r11 == 0) goto L_0x0120;
    L_0x010b:
        r4 = r9.mSpanIndex;
        r3 = java.lang.Math.min(r3, r5);
        r2 = java.lang.Math.max(r2, r6);
        r3 = r3 - r2;
        r15 = r3;
        r16 = r8;
        r17 = r10;
        r10 = r21;
        r8 = r4;
        r4 = r1;
        goto L_0x013b;
    L_0x0120:
        r8 = r9.mSpanIndex;
        r3 = java.lang.Math.min(r3, r5);
        r2 = java.lang.Math.max(r2, r6);
        r3 = r3 - r2;
        r10 = r1;
        r16 = r3;
        r17 = r8;
        r8 = r19;
        goto L_0x013b;
    L_0x0133:
        r16 = r8;
        r17 = r10;
        r8 = r19;
        r10 = r21;
    L_0x013b:
        r7 = r7 + r12;
        r1 = r26;
        r2 = r27;
        r3 = r18;
        r11 = r20;
        r9 = 1;
        goto L_0x005d;
    L_0x0147:
        r21 = r10;
        if (r4 == 0) goto L_0x014c;
    L_0x014b:
        goto L_0x014e;
    L_0x014c:
        r4 = r21;
    L_0x014e:
        return r4;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.support.widget.GridLayoutManager.onFocusSearchFailed(android.view.View, int, org.telegram.messenger.support.widget.RecyclerView$Recycler, org.telegram.messenger.support.widget.RecyclerView$State):android.view.View");
    }

    public boolean supportsPredictiveItemAnimations() {
        return this.mPendingSavedState == null && !this.mPendingSpanCountChange;
    }
}
