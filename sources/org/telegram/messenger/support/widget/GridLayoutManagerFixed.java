package org.telegram.messenger.support.widget;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import java.util.ArrayList;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.Recycler;

public class GridLayoutManagerFixed extends GridLayoutManager {
    private ArrayList<View> additionalViews = new ArrayList(4);
    private boolean canScrollVertically = true;

    /* Access modifiers changed, original: protected */
    public boolean hasSiblingChild(int i) {
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

    /* Access modifiers changed, original: protected */
    public void recycleViewsFromStart(Recycler recycler, int i) {
        if (i >= 0) {
            int childCount = getChildCount();
            if (this.mShouldReverseLayout) {
                childCount--;
                for (int i2 = childCount; i2 >= 0; i2--) {
                    View childAt = getChildAt(i2);
                    if (childAt.getBottom() + ((LayoutParams) childAt.getLayoutParams()).bottomMargin > i || childAt.getTop() + childAt.getHeight() > i) {
                        recycleChildren(recycler, childCount, i2);
                        return;
                    }
                }
            } else {
                for (int i3 = 0; i3 < childCount; i3++) {
                    View childAt2 = getChildAt(i3);
                    if (this.mOrientationHelper.getDecoratedEnd(childAt2) > i || this.mOrientationHelper.getTransformedEndWithDecoration(childAt2) > i) {
                        recycleChildren(recycler, 0, i3);
                        break;
                    }
                }
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public int[] calculateItemBorders(int[] iArr, int i, int i2) {
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

    /* Access modifiers changed, original: protected */
    public void measureChild(View view, int i, boolean z) {
        GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        Rect rect = layoutParams.mDecorInsets;
        int i2 = ((rect.left + rect.right) + layoutParams.leftMargin) + layoutParams.rightMargin;
        measureChildWithDecorationsAndMargin(view, LayoutManager.getChildMeasureSpec(this.mCachedBorders[layoutParams.mSpanSize], i, i2, layoutParams.width, false), LayoutManager.getChildMeasureSpec(this.mOrientationHelper.getTotalSpace(), getHeightMode(), ((rect.top + rect.bottom) + layoutParams.topMargin) + layoutParams.bottomMargin, layoutParams.height, true), z);
    }

    /* Access modifiers changed, original: 0000 */
    /* JADX WARNING: Missing block: B:73:0x0194, code skipped:
            if (r9.mLayoutDirection != -1) goto L_0x0198;
     */
    public void layoutChunk(org.telegram.messenger.support.widget.RecyclerView.Recycler r26, org.telegram.messenger.support.widget.RecyclerView.State r27, org.telegram.messenger.support.widget.LinearLayoutManager.LayoutState r28, org.telegram.messenger.support.widget.LinearLayoutManager.LayoutChunkResult r29) {
        /*
        r25 = this;
        r6 = r25;
        r7 = r26;
        r8 = r27;
        r9 = r28;
        r10 = r29;
        r0 = r6.mOrientationHelper;
        r11 = r0.getModeInOther();
        r0 = r9.mItemDirection;
        r12 = 0;
        r13 = 1;
        if (r0 != r13) goto L_0x0018;
    L_0x0016:
        r14 = 1;
        goto L_0x0019;
    L_0x0018:
        r14 = 0;
    L_0x0019:
        r10.mConsumed = r12;
        r0 = r9.mCurrentPosition;
        r1 = r9.mLayoutDirection;
        r15 = -1;
        if (r1 == r15) goto L_0x0074;
    L_0x0022:
        r1 = r6.hasSiblingChild(r0);
        if (r1 == 0) goto L_0x0074;
    L_0x0028:
        r1 = r9.mCurrentPosition;
        r1 = r1 + r13;
        r1 = r6.findViewByPosition(r1);
        if (r1 != 0) goto L_0x0074;
    L_0x0031:
        r1 = r9.mCurrentPosition;
        r1 = r1 + r13;
        r1 = r6.hasSiblingChild(r1);
        if (r1 == 0) goto L_0x0041;
    L_0x003a:
        r1 = r9.mCurrentPosition;
        r1 = r1 + 3;
        r9.mCurrentPosition = r1;
        goto L_0x0047;
    L_0x0041:
        r1 = r9.mCurrentPosition;
        r1 = r1 + 2;
        r9.mCurrentPosition = r1;
    L_0x0047:
        r1 = r9.mCurrentPosition;
        r2 = r1;
    L_0x004a:
        if (r2 <= r0) goto L_0x0072;
    L_0x004c:
        r3 = r9.next(r7);
        r4 = r6.additionalViews;
        r4.add(r3);
        if (r2 == r1) goto L_0x006f;
    L_0x0057:
        r4 = r6.mDecorInsets;
        r6.calculateItemDecorationsForChild(r3, r4);
        r6.measureChild(r3, r11, r12);
        r4 = r6.mOrientationHelper;
        r3 = r4.getDecoratedMeasurement(r3);
        r4 = r9.mOffset;
        r4 = r4 - r3;
        r9.mOffset = r4;
        r4 = r9.mAvailable;
        r4 = r4 + r3;
        r9.mAvailable = r4;
    L_0x006f:
        r2 = r2 + -1;
        goto L_0x004a;
    L_0x0072:
        r9.mCurrentPosition = r1;
    L_0x0074:
        r0 = 1;
    L_0x0075:
        if (r0 == 0) goto L_0x02ab;
    L_0x0077:
        r0 = r6.mSpanCount;
        r1 = r6.additionalViews;
        r1 = r1.isEmpty();
        r1 = r1 ^ r13;
        r2 = r9.mCurrentPosition;
        r16 = r1;
        r4 = 0;
        r5 = 0;
    L_0x0086:
        r1 = r6.mSpanCount;
        if (r5 >= r1) goto L_0x00d6;
    L_0x008a:
        r1 = r9.hasMore(r8);
        if (r1 == 0) goto L_0x00d6;
    L_0x0090:
        if (r0 <= 0) goto L_0x00d6;
    L_0x0092:
        r1 = r9.mCurrentPosition;
        r2 = r6.getSpanSize(r7, r8, r1);
        r0 = r0 - r2;
        if (r0 >= 0) goto L_0x009c;
    L_0x009b:
        goto L_0x00d6;
    L_0x009c:
        r3 = r6.additionalViews;
        r3 = r3.isEmpty();
        if (r3 != 0) goto L_0x00b7;
    L_0x00a4:
        r3 = r6.additionalViews;
        r3 = r3.get(r12);
        r3 = (android.view.View) r3;
        r15 = r6.additionalViews;
        r15.remove(r12);
        r15 = r9.mCurrentPosition;
        r15 = r15 - r13;
        r9.mCurrentPosition = r15;
        goto L_0x00bb;
    L_0x00b7:
        r3 = r9.next(r7);
    L_0x00bb:
        if (r3 != 0) goto L_0x00be;
    L_0x00bd:
        goto L_0x00d6;
    L_0x00be:
        r4 = r4 + r2;
        r2 = r6.mSet;
        r2[r5] = r3;
        r5 = r5 + 1;
        r2 = r9.mLayoutDirection;
        r3 = -1;
        if (r2 != r3) goto L_0x00d4;
    L_0x00ca:
        if (r0 > 0) goto L_0x00d4;
    L_0x00cc:
        r1 = r6.hasSiblingChild(r1);
        if (r1 == 0) goto L_0x00d4;
    L_0x00d2:
        r16 = 1;
    L_0x00d4:
        r15 = -1;
        goto L_0x0086;
    L_0x00d6:
        if (r5 != 0) goto L_0x00db;
    L_0x00d8:
        r10.mFinished = r13;
        return;
    L_0x00db:
        r0 = r25;
        r1 = r26;
        r2 = r27;
        r3 = r5;
        r15 = r5;
        r5 = r14;
        r0.assignSpans(r1, r2, r3, r4, r5);
        r0 = 0;
        r5 = 0;
        r17 = 0;
    L_0x00eb:
        if (r0 >= r15) goto L_0x0137;
    L_0x00ed:
        r1 = r6.mSet;
        r1 = r1[r0];
        r2 = r9.mScrapList;
        if (r2 != 0) goto L_0x00ff;
    L_0x00f5:
        if (r14 == 0) goto L_0x00fb;
    L_0x00f7:
        r6.addView(r1);
        goto L_0x0108;
    L_0x00fb:
        r6.addView(r1, r12);
        goto L_0x0108;
    L_0x00ff:
        if (r14 == 0) goto L_0x0105;
    L_0x0101:
        r6.addDisappearingView(r1);
        goto L_0x0108;
    L_0x0105:
        r6.addDisappearingView(r1, r12);
    L_0x0108:
        r2 = r6.mDecorInsets;
        r6.calculateItemDecorationsForChild(r1, r2);
        r6.measureChild(r1, r11, r12);
        r2 = r6.mOrientationHelper;
        r2 = r2.getDecoratedMeasurement(r1);
        if (r2 <= r5) goto L_0x0119;
    L_0x0118:
        r5 = r2;
    L_0x0119:
        r2 = r1.getLayoutParams();
        r2 = (org.telegram.messenger.support.widget.GridLayoutManager.LayoutParams) r2;
        r3 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r4 = r6.mOrientationHelper;
        r1 = r4.getDecoratedMeasurementInOther(r1);
        r1 = (float) r1;
        r1 = r1 * r3;
        r2 = r2.mSpanSize;
        r2 = (float) r2;
        r1 = r1 / r2;
        r2 = (r1 > r17 ? 1 : (r1 == r17 ? 0 : -1));
        if (r2 <= 0) goto L_0x0134;
    L_0x0132:
        r17 = r1;
    L_0x0134:
        r0 = r0 + 1;
        goto L_0x00eb;
    L_0x0137:
        r0 = 0;
    L_0x0138:
        if (r0 >= r15) goto L_0x0186;
    L_0x013a:
        r1 = r6.mSet;
        r1 = r1[r0];
        r2 = r6.mOrientationHelper;
        r2 = r2.getDecoratedMeasurement(r1);
        if (r2 == r5) goto L_0x017e;
    L_0x0146:
        r2 = r1.getLayoutParams();
        r2 = (org.telegram.messenger.support.widget.GridLayoutManager.LayoutParams) r2;
        r3 = r2.mDecorInsets;
        r4 = r3.top;
        r13 = r3.bottom;
        r4 = r4 + r13;
        r13 = r2.topMargin;
        r4 = r4 + r13;
        r13 = r2.bottomMargin;
        r4 = r4 + r13;
        r13 = r3.left;
        r3 = r3.right;
        r13 = r13 + r3;
        r3 = r2.leftMargin;
        r13 = r13 + r3;
        r3 = r2.rightMargin;
        r13 = r13 + r3;
        r3 = r6.mCachedBorders;
        r12 = r2.mSpanSize;
        r3 = r3[r12];
        r2 = r2.width;
        r12 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r7 = 0;
        r2 = org.telegram.messenger.support.widget.RecyclerView.LayoutManager.getChildMeasureSpec(r3, r12, r13, r2, r7);
        r3 = r5 - r4;
        r3 = android.view.View.MeasureSpec.makeMeasureSpec(r3, r12);
        r4 = 1;
        r6.measureChildWithDecorationsAndMargin(r1, r2, r3, r4);
        goto L_0x017f;
    L_0x017e:
        r7 = 0;
    L_0x017f:
        r0 = r0 + 1;
        r7 = r26;
        r12 = 0;
        r13 = 1;
        goto L_0x0138;
    L_0x0186:
        r7 = 0;
        r0 = r6.mSet;
        r0 = r0[r7];
        r0 = r6.shouldLayoutChildFromOpositeSide(r0);
        if (r0 == 0) goto L_0x0197;
    L_0x0191:
        r1 = r9.mLayoutDirection;
        r2 = -1;
        if (r1 == r2) goto L_0x019f;
    L_0x0196:
        goto L_0x0198;
    L_0x0197:
        r2 = -1;
    L_0x0198:
        if (r0 != 0) goto L_0x021c;
    L_0x019a:
        r0 = r9.mLayoutDirection;
        r1 = 1;
        if (r0 != r1) goto L_0x021c;
    L_0x019f:
        r0 = r9.mLayoutDirection;
        if (r0 != r2) goto L_0x01af;
    L_0x01a3:
        r0 = r9.mOffset;
        r1 = r10.mConsumed;
        r0 = r0 - r1;
        r1 = r0 - r5;
        r18 = r0;
        r13 = r1;
        r12 = 0;
        goto L_0x01bd;
    L_0x01af:
        r0 = r9.mOffset;
        r1 = r10.mConsumed;
        r0 = r0 + r1;
        r1 = r0 + r5;
        r12 = r25.getWidth();
        r13 = r0;
        r18 = r1;
    L_0x01bd:
        r0 = r15 + -1;
        r24 = r12;
        r12 = r0;
        r0 = r24;
    L_0x01c4:
        if (r12 < 0) goto L_0x0218;
    L_0x01c6:
        r1 = r6.mSet;
        r15 = r1[r12];
        r1 = r15.getLayoutParams();
        r19 = r1;
        r19 = (org.telegram.messenger.support.widget.GridLayoutManager.LayoutParams) r19;
        r1 = r6.mOrientationHelper;
        r1 = r1.getDecoratedMeasurementInOther(r15);
        r2 = r9.mLayoutDirection;
        r3 = 1;
        if (r2 != r3) goto L_0x01de;
    L_0x01dd:
        r0 = r0 - r1;
    L_0x01de:
        r20 = r0;
        r21 = r20 + r1;
        r0 = r25;
        r1 = r15;
        r2 = r20;
        r3 = r13;
        r4 = r21;
        r22 = r5;
        r5 = r18;
        r0.layoutDecoratedWithMargins(r1, r2, r3, r4, r5);
        r0 = r9.mLayoutDirection;
        r1 = -1;
        if (r0 != r1) goto L_0x01f9;
    L_0x01f6:
        r0 = r21;
        goto L_0x01fb;
    L_0x01f9:
        r0 = r20;
    L_0x01fb:
        r1 = r19.isItemRemoved();
        if (r1 != 0) goto L_0x0207;
    L_0x0201:
        r1 = r19.isItemChanged();
        if (r1 == 0) goto L_0x020a;
    L_0x0207:
        r1 = 1;
        r10.mIgnoreConsumed = r1;
    L_0x020a:
        r1 = r10.mFocusable;
        r2 = r15.hasFocusable();
        r1 = r1 | r2;
        r10.mFocusable = r1;
        r12 = r12 + -1;
        r5 = r22;
        goto L_0x01c4;
    L_0x0218:
        r22 = r5;
        goto L_0x0294;
    L_0x021c:
        r22 = r5;
        r0 = r9.mLayoutDirection;
        r1 = -1;
        if (r0 != r1) goto L_0x0232;
    L_0x0223:
        r0 = r9.mOffset;
        r1 = r10.mConsumed;
        r0 = r0 - r1;
        r1 = r0 - r22;
        r12 = r25.getWidth();
        r18 = r0;
        r13 = r1;
        goto L_0x023d;
    L_0x0232:
        r0 = r9.mOffset;
        r1 = r10.mConsumed;
        r0 = r0 + r1;
        r5 = r0 + r22;
        r13 = r0;
        r18 = r5;
        r12 = 0;
    L_0x023d:
        r0 = r12;
        r12 = 0;
    L_0x023f:
        if (r12 >= r15) goto L_0x0294;
    L_0x0241:
        r1 = r6.mSet;
        r5 = r1[r12];
        r1 = r5.getLayoutParams();
        r19 = r1;
        r19 = (org.telegram.messenger.support.widget.GridLayoutManager.LayoutParams) r19;
        r1 = r6.mOrientationHelper;
        r1 = r1.getDecoratedMeasurementInOther(r5);
        r2 = r9.mLayoutDirection;
        r3 = -1;
        if (r2 != r3) goto L_0x0259;
    L_0x0258:
        r0 = r0 - r1;
    L_0x0259:
        r20 = r0;
        r21 = r20 + r1;
        r0 = r25;
        r1 = r5;
        r2 = r20;
        r3 = r13;
        r4 = r21;
        r23 = r5;
        r5 = r18;
        r0.layoutDecoratedWithMargins(r1, r2, r3, r4, r5);
        r0 = r9.mLayoutDirection;
        r1 = -1;
        if (r0 == r1) goto L_0x0274;
    L_0x0271:
        r0 = r21;
        goto L_0x0276;
    L_0x0274:
        r0 = r20;
    L_0x0276:
        r2 = r19.isItemRemoved();
        if (r2 != 0) goto L_0x0285;
    L_0x027c:
        r2 = r19.isItemChanged();
        if (r2 == 0) goto L_0x0283;
    L_0x0282:
        goto L_0x0285;
    L_0x0283:
        r2 = 1;
        goto L_0x0288;
    L_0x0285:
        r2 = 1;
        r10.mIgnoreConsumed = r2;
    L_0x0288:
        r3 = r10.mFocusable;
        r4 = r23.hasFocusable();
        r3 = r3 | r4;
        r10.mFocusable = r3;
        r12 = r12 + 1;
        goto L_0x023f;
    L_0x0294:
        r1 = -1;
        r2 = 1;
        r0 = r10.mConsumed;
        r0 = r0 + r22;
        r10.mConsumed = r0;
        r0 = r6.mSet;
        r3 = 0;
        java.util.Arrays.fill(r0, r3);
        r7 = r26;
        r0 = r16;
        r12 = 0;
        r13 = 1;
        r15 = -1;
        goto L_0x0075;
    L_0x02ab:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.support.widget.GridLayoutManagerFixed.layoutChunk(org.telegram.messenger.support.widget.RecyclerView$Recycler, org.telegram.messenger.support.widget.RecyclerView$State, org.telegram.messenger.support.widget.LinearLayoutManager$LayoutState, org.telegram.messenger.support.widget.LinearLayoutManager$LayoutChunkResult):void");
    }
}
