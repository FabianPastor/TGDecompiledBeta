package org.telegram.ui.Components.voip;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;

public class VoIPButtonsLayout extends FrameLayout {
    int childPadding;
    int childWidth;
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
        View.MeasureSpec.getSize(i2);
        this.visibleChildCount = 0;
        for (int i3 = 0; i3 < getChildCount(); i3++) {
            if (getChildAt(i3).getVisibility() != 8) {
                this.visibleChildCount++;
            }
        }
        this.childWidth = AndroidUtilities.dp(68.0f);
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
        setMeasuredDimension(size, Math.max(i4, AndroidUtilities.dp(80.0f)));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int childCount = (int) ((((float) (getChildCount() - this.visibleChildCount)) / 2.0f) * ((float) (this.childWidth + (this.childPadding * 2))));
        for (int i5 = 0; i5 < getChildCount(); i5++) {
            View childAt = getChildAt(i5);
            if (childAt.getVisibility() != 8) {
                int i6 = this.childPadding;
                childAt.layout(childCount + i6, 0, i6 + childCount + childAt.getMeasuredWidth(), childAt.getMeasuredHeight());
                childCount += (this.childPadding * 2) + childAt.getMeasuredWidth();
            }
        }
    }
}
