package org.telegram.ui.Components;

import android.view.View;

public abstract class IntSeekBarAccessibilityDelegate extends SeekBarAccessibilityDelegate {
    /* access modifiers changed from: protected */
    public int getDelta() {
        return 1;
    }

    /* access modifiers changed from: protected */
    public abstract int getMaxValue();

    /* access modifiers changed from: protected */
    public int getMinValue() {
        return 0;
    }

    /* access modifiers changed from: protected */
    public abstract int getProgress();

    /* access modifiers changed from: protected */
    public abstract void setProgress(int i);

    /* access modifiers changed from: protected */
    public void doScroll(View view, boolean z) {
        int delta = getDelta();
        if (z) {
            delta *= -1;
        }
        setProgress(Math.min(getMaxValue(), Math.max(getMinValue(), getProgress() + delta)));
    }

    /* access modifiers changed from: protected */
    public boolean canScrollBackward(View view) {
        return getProgress() > getMinValue();
    }

    /* access modifiers changed from: protected */
    public boolean canScrollForward(View view) {
        return getProgress() < getMaxValue();
    }
}
