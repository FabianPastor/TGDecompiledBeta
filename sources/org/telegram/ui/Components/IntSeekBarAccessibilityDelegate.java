package org.telegram.ui.Components;

import android.view.View;

public abstract class IntSeekBarAccessibilityDelegate extends SeekBarAccessibilityDelegate {
    /* access modifiers changed from: protected */
    public abstract int getMaxValue();

    /* access modifiers changed from: protected */
    public abstract int getProgress();

    /* access modifiers changed from: protected */
    public abstract void setProgress(int i);

    /* access modifiers changed from: protected */
    public void doScroll(View host, boolean backward) {
        int delta = getDelta();
        if (backward) {
            delta *= -1;
        }
        setProgress(Math.min(getMaxValue(), Math.max(getMinValue(), getProgress() + delta)));
    }

    /* access modifiers changed from: protected */
    public boolean canScrollBackward(View host) {
        return getProgress() > getMinValue();
    }

    /* access modifiers changed from: protected */
    public boolean canScrollForward(View host) {
        return getProgress() < getMaxValue();
    }

    /* access modifiers changed from: protected */
    public int getMinValue() {
        return 0;
    }

    /* access modifiers changed from: protected */
    public int getDelta() {
        return 1;
    }
}
