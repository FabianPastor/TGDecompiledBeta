package org.telegram.ui.Components.voip;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;

public class VoIPButtonsLayout extends FrameLayout {
    int childPadding;
    private int childSize = 68;
    int childWidth;
    private boolean startPadding = true;
    int visibleChildCount;

    public VoIPButtonsLayout(Context context) {
        super(context);
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (!isEnabled()) {
            return false;
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        this.visibleChildCount = 0;
        for (int i3 = 0; i3 < getChildCount(); i3++) {
            if (getChildAt(i3).getVisibility() != 8) {
                this.visibleChildCount++;
            }
        }
        this.childWidth = AndroidUtilities.dp((float) this.childSize);
        this.childPadding = ((size / getChildCount()) - this.childWidth) / 2;
        int i4 = 0;
        for (int i5 = 0; i5 < getChildCount(); i5++) {
            if (getChildAt(i5).getVisibility() != 8) {
                getChildAt(i5).measure(View.MeasureSpec.makeMeasureSpec(this.childWidth, NUM), i2);
                if (getChildAt(i5).getMeasuredHeight() > i4) {
                    i4 = getChildAt(i5).getMeasuredHeight();
                }
            }
        }
        setMeasuredDimension(size, Math.max(i4, AndroidUtilities.dp(this.startPadding ? 63.0f : 80.0f)));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (this.startPadding) {
            int childCount = (int) ((((float) (getChildCount() - this.visibleChildCount)) / 2.0f) * ((float) (this.childWidth + (this.childPadding * 2))));
            for (int i5 = 0; i5 < getChildCount(); i5++) {
                View childAt = getChildAt(i5);
                if (childAt.getVisibility() != 8) {
                    int i6 = this.childPadding;
                    childAt.layout(childCount + i6, 0, i6 + childCount + childAt.getMeasuredWidth(), childAt.getMeasuredHeight());
                    childCount += (this.childPadding * 2) + childAt.getMeasuredWidth();
                }
            }
            return;
        }
        int measuredWidth = this.visibleChildCount > 0 ? (getMeasuredWidth() - this.childWidth) / (this.visibleChildCount - 1) : 0;
        int i7 = 0;
        for (int i8 = 0; i8 < getChildCount(); i8++) {
            View childAt2 = getChildAt(i8);
            if (childAt2.getVisibility() != 8) {
                int i9 = i7 * measuredWidth;
                childAt2.layout(i9, 0, childAt2.getMeasuredWidth() + i9, childAt2.getMeasuredHeight());
                i7++;
            }
        }
    }

    public void setChildSize(int i) {
        this.childSize = i;
    }

    public void setUseStartPadding(boolean z) {
        this.startPadding = z;
    }
}
