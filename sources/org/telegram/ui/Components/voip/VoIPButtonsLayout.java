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

    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!isEnabled()) {
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        this.visibleChildCount = 0;
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i).getVisibility() != 8) {
                this.visibleChildCount++;
            }
        }
        this.childWidth = AndroidUtilities.dp((float) this.childSize);
        int maxChildHeigth = 0;
        this.childPadding = ((width / getChildCount()) - this.childWidth) / 2;
        for (int i2 = 0; i2 < getChildCount(); i2++) {
            if (getChildAt(i2).getVisibility() != 8) {
                getChildAt(i2).measure(View.MeasureSpec.makeMeasureSpec(this.childWidth, NUM), heightMeasureSpec);
                if (getChildAt(i2).getMeasuredHeight() > maxChildHeigth) {
                    maxChildHeigth = getChildAt(i2).getMeasuredHeight();
                }
            }
        }
        setMeasuredDimension(width, Math.max(maxChildHeigth, AndroidUtilities.dp(80.0f)));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (this.startPadding) {
            int startFrom = (int) ((((float) (getChildCount() - this.visibleChildCount)) / 2.0f) * ((float) (this.childWidth + (this.childPadding * 2))));
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child.getVisibility() != 8) {
                    int i2 = this.childPadding;
                    child.layout(startFrom + i2, 0, i2 + startFrom + child.getMeasuredWidth(), child.getMeasuredHeight());
                    startFrom += (this.childPadding * 2) + child.getMeasuredWidth();
                }
            }
            return;
        }
        int padding = this.visibleChildCount > 0 ? (getMeasuredWidth() - this.childWidth) / (this.visibleChildCount - 1) : 0;
        int k = 0;
        for (int i3 = 0; i3 < getChildCount(); i3++) {
            View child2 = getChildAt(i3);
            if (child2.getVisibility() != 8) {
                child2.layout(k * padding, 0, (k * padding) + child2.getMeasuredWidth(), child2.getMeasuredHeight());
                k++;
            }
        }
    }

    public void setChildSize(int childSize2) {
        this.childSize = childSize2;
    }

    public void setUseStartPadding(boolean startPadding2) {
        this.startPadding = startPadding2;
    }
}
