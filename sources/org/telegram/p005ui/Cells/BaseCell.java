package org.telegram.p005ui.Cells;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

/* renamed from: org.telegram.ui.Cells.BaseCell */
public abstract class BaseCell extends ViewGroup {
    private boolean checkingForLongPress = false;
    private CheckForLongPress pendingCheckForLongPress = null;
    private CheckForTap pendingCheckForTap = null;
    private int pressCount = 0;

    /* renamed from: org.telegram.ui.Cells.BaseCell$CheckForLongPress */
    class CheckForLongPress implements Runnable {
        public int currentPressCount;

        CheckForLongPress() {
        }

        public void run() {
            if (BaseCell.this.checkingForLongPress && BaseCell.this.getParent() != null && this.currentPressCount == BaseCell.this.pressCount) {
                BaseCell.this.checkingForLongPress = false;
                BaseCell.this.performHapticFeedback(0);
                BaseCell.this.onLongPress();
                MotionEvent event = MotionEvent.obtain(0, 0, 3, 0.0f, 0.0f, 0);
                BaseCell.this.onTouchEvent(event);
                event.recycle();
            }
        }
    }

    /* renamed from: org.telegram.ui.Cells.BaseCell$CheckForTap */
    private final class CheckForTap implements Runnable {
        private CheckForTap() {
        }

        public void run() {
            if (BaseCell.this.pendingCheckForLongPress == null) {
                BaseCell.this.pendingCheckForLongPress = new CheckForLongPress();
            }
            BaseCell.this.pendingCheckForLongPress.currentPressCount = BaseCell.access$104(BaseCell.this);
            BaseCell.this.postDelayed(BaseCell.this.pendingCheckForLongPress, (long) (ViewConfiguration.getLongPressTimeout() - ViewConfiguration.getTapTimeout()));
        }
    }

    static /* synthetic */ int access$104(BaseCell x0) {
        int i = x0.pressCount + 1;
        x0.pressCount = i;
        return i;
    }

    public BaseCell(Context context) {
        super(context);
        setWillNotDraw(false);
    }

    public static void setDrawableBounds(Drawable drawable, int x, int y) {
        BaseCell.setDrawableBounds(drawable, x, y, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    }

    public static void setDrawableBounds(Drawable drawable, float x, float y) {
        BaseCell.setDrawableBounds(drawable, (int) x, (int) y, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    }

    public static void setDrawableBounds(Drawable drawable, int x, int y, int w, int h) {
        if (drawable != null) {
            drawable.setBounds(x, y, x + w, y + h);
        }
    }

    protected void startCheckLongPress() {
        if (!this.checkingForLongPress) {
            this.checkingForLongPress = true;
            if (this.pendingCheckForTap == null) {
                this.pendingCheckForTap = new CheckForTap();
            }
            postDelayed(this.pendingCheckForTap, (long) ViewConfiguration.getTapTimeout());
        }
    }

    protected void cancelCheckLongPress() {
        this.checkingForLongPress = false;
        if (this.pendingCheckForLongPress != null) {
            removeCallbacks(this.pendingCheckForLongPress);
        }
        if (this.pendingCheckForTap != null) {
            removeCallbacks(this.pendingCheckForTap);
        }
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    protected void onLongPress() {
    }
}
