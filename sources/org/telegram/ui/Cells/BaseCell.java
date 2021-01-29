package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

public abstract class BaseCell extends ViewGroup {
    /* access modifiers changed from: private */
    public boolean checkingForLongPress = false;
    /* access modifiers changed from: private */
    public CheckForLongPress pendingCheckForLongPress = null;
    private CheckForTap pendingCheckForTap = null;
    /* access modifiers changed from: private */
    public int pressCount = 0;

    public boolean hasOverlappingRendering() {
        return false;
    }

    /* access modifiers changed from: protected */
    public void onLongPress() {
    }

    static /* synthetic */ int access$104(BaseCell baseCell) {
        int i = baseCell.pressCount + 1;
        baseCell.pressCount = i;
        return i;
    }

    private final class CheckForTap implements Runnable {
        private CheckForTap() {
        }

        public void run() {
            if (BaseCell.this.pendingCheckForLongPress == null) {
                BaseCell baseCell = BaseCell.this;
                CheckForLongPress unused = baseCell.pendingCheckForLongPress = new CheckForLongPress();
            }
            BaseCell.this.pendingCheckForLongPress.currentPressCount = BaseCell.access$104(BaseCell.this);
            BaseCell baseCell2 = BaseCell.this;
            baseCell2.postDelayed(baseCell2.pendingCheckForLongPress, (long) (ViewConfiguration.getLongPressTimeout() - ViewConfiguration.getTapTimeout()));
        }
    }

    class CheckForLongPress implements Runnable {
        public int currentPressCount;

        CheckForLongPress() {
        }

        public void run() {
            if (BaseCell.this.checkingForLongPress && BaseCell.this.getParent() != null && this.currentPressCount == BaseCell.this.pressCount) {
                boolean unused = BaseCell.this.checkingForLongPress = false;
                BaseCell.this.performHapticFeedback(0);
                BaseCell.this.onLongPress();
                MotionEvent obtain = MotionEvent.obtain(0, 0, 3, 0.0f, 0.0f, 0);
                BaseCell.this.onTouchEvent(obtain);
                obtain.recycle();
            }
        }
    }

    public BaseCell(Context context) {
        super(context);
        setWillNotDraw(false);
        setFocusable(true);
    }

    public static void setDrawableBounds(Drawable drawable, int i, int i2) {
        setDrawableBounds(drawable, i, i2, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    }

    public static void setDrawableBounds(Drawable drawable, float f, float f2) {
        setDrawableBounds(drawable, (int) f, (int) f2, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    }

    public static void setDrawableBounds(Drawable drawable, int i, int i2, int i3, int i4) {
        if (drawable != null) {
            drawable.setBounds(i, i2, i3 + i, i4 + i2);
        }
    }

    public static void setDrawableBounds(Drawable drawable, float f, float f2, int i, int i2) {
        if (drawable != null) {
            int i3 = (int) f;
            int i4 = (int) f2;
            drawable.setBounds(i3, i4, i + i3, i2 + i4);
        }
    }

    /* access modifiers changed from: protected */
    public void startCheckLongPress() {
        if (!this.checkingForLongPress) {
            this.checkingForLongPress = true;
            if (this.pendingCheckForTap == null) {
                this.pendingCheckForTap = new CheckForTap();
            }
            postDelayed(this.pendingCheckForTap, (long) ViewConfiguration.getTapTimeout());
        }
    }

    /* access modifiers changed from: protected */
    public void cancelCheckLongPress() {
        this.checkingForLongPress = false;
        CheckForLongPress checkForLongPress = this.pendingCheckForLongPress;
        if (checkForLongPress != null) {
            removeCallbacks(checkForLongPress);
        }
        CheckForTap checkForTap = this.pendingCheckForTap;
        if (checkForTap != null) {
            removeCallbacks(checkForTap);
        }
    }
}
