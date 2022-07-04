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

    static /* synthetic */ int access$104(BaseCell x0) {
        int i = x0.pressCount + 1;
        x0.pressCount = i;
        return i;
    }

    private final class CheckForTap implements Runnable {
        private CheckForTap() {
        }

        public void run() {
            if (BaseCell.this.pendingCheckForLongPress == null) {
                CheckForLongPress unused = BaseCell.this.pendingCheckForLongPress = new CheckForLongPress();
            }
            BaseCell.this.pendingCheckForLongPress.currentPressCount = BaseCell.access$104(BaseCell.this);
            BaseCell baseCell = BaseCell.this;
            baseCell.postDelayed(baseCell.pendingCheckForLongPress, (long) (ViewConfiguration.getLongPressTimeout() - ViewConfiguration.getTapTimeout()));
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
                if (BaseCell.this.onLongPress()) {
                    MotionEvent event = MotionEvent.obtain(0, 0, 3, 0.0f, 0.0f, 0);
                    BaseCell.this.onTouchEvent(event);
                    event.recycle();
                }
            }
        }
    }

    public BaseCell(Context context) {
        super(context);
        setWillNotDraw(false);
        setFocusable(true);
        setHapticFeedbackEnabled(true);
    }

    public static void setDrawableBounds(Drawable drawable, int x, int y) {
        setDrawableBounds(drawable, x, y, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    }

    public static void setDrawableBounds(Drawable drawable, float x, float y) {
        setDrawableBounds(drawable, (int) x, (int) y, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    }

    public static void setDrawableBounds(Drawable drawable, int x, int y, int w, int h) {
        if (drawable != null) {
            drawable.setBounds(x, y, x + w, y + h);
        }
    }

    public static void setDrawableBounds(Drawable drawable, float x, float y, int w, int h) {
        if (drawable != null) {
            drawable.setBounds((int) x, (int) y, ((int) x) + w, ((int) y) + h);
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

    public boolean hasOverlappingRendering() {
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean onLongPress() {
        return true;
    }
}
