package org.telegram.ui.Components;

import android.view.View;
/* loaded from: classes3.dex */
public abstract class IntSeekBarAccessibilityDelegate extends SeekBarAccessibilityDelegate {
    protected int getDelta() {
        return 1;
    }

    protected abstract int getMaxValue();

    protected int getMinValue() {
        return 0;
    }

    protected abstract int getProgress();

    protected abstract void setProgress(int i);

    @Override // org.telegram.ui.Components.SeekBarAccessibilityDelegate
    protected void doScroll(View view, boolean z) {
        int delta = getDelta();
        if (z) {
            delta *= -1;
        }
        setProgress(Math.min(getMaxValue(), Math.max(getMinValue(), getProgress() + delta)));
    }

    @Override // org.telegram.ui.Components.SeekBarAccessibilityDelegate
    protected boolean canScrollBackward(View view) {
        return getProgress() > getMinValue();
    }

    @Override // org.telegram.ui.Components.SeekBarAccessibilityDelegate
    protected boolean canScrollForward(View view) {
        return getProgress() < getMaxValue();
    }
}
