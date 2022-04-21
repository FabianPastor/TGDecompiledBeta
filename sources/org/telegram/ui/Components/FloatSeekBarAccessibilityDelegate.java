package org.telegram.ui.Components;

import android.os.Bundle;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;

public abstract class FloatSeekBarAccessibilityDelegate extends SeekBarAccessibilityDelegate {
    private final boolean setPercentsEnabled;

    /* access modifiers changed from: protected */
    public abstract float getProgress();

    /* access modifiers changed from: protected */
    public abstract void setProgress(float f);

    public FloatSeekBarAccessibilityDelegate() {
        this(false);
    }

    public FloatSeekBarAccessibilityDelegate(boolean setPercentsEnabled2) {
        this.setPercentsEnabled = setPercentsEnabled2;
    }

    public void onInitializeAccessibilityNodeInfoInternal(View host, AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfoInternal(host, info);
        if (this.setPercentsEnabled) {
            AccessibilityNodeInfoCompat infoCompat = AccessibilityNodeInfoCompat.wrap(info);
            infoCompat.addAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SET_PROGRESS);
            infoCompat.setRangeInfo(AccessibilityNodeInfoCompat.RangeInfoCompat.obtain(1, getMinValue(), getMaxValue(), getProgress()));
        }
    }

    public boolean performAccessibilityActionInternal(View host, int action, Bundle args) {
        if (super.performAccessibilityActionInternal(host, action, args)) {
            return true;
        }
        if (action != AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SET_PROGRESS.getId()) {
            return false;
        }
        setProgress(args.getFloat("android.view.accessibility.action.ARGUMENT_PROGRESS_VALUE"));
        return true;
    }

    /* access modifiers changed from: protected */
    public void doScroll(View host, boolean backward) {
        float delta = getDelta();
        if (backward) {
            delta *= -1.0f;
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
    public float getMinValue() {
        return 0.0f;
    }

    /* access modifiers changed from: protected */
    public float getMaxValue() {
        return 1.0f;
    }

    /* access modifiers changed from: protected */
    public float getDelta() {
        return 0.05f;
    }
}
