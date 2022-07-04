package org.telegram.ui.Components;

import android.os.Bundle;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;

public abstract class FloatSeekBarAccessibilityDelegate extends SeekBarAccessibilityDelegate {
    private final boolean setPercentsEnabled;

    /* access modifiers changed from: protected */
    public float getDelta() {
        return 0.05f;
    }

    /* access modifiers changed from: protected */
    public float getMaxValue() {
        return 1.0f;
    }

    /* access modifiers changed from: protected */
    public float getMinValue() {
        return 0.0f;
    }

    /* access modifiers changed from: protected */
    public abstract float getProgress();

    /* access modifiers changed from: protected */
    public abstract void setProgress(float f);

    public FloatSeekBarAccessibilityDelegate() {
        this(false);
    }

    public FloatSeekBarAccessibilityDelegate(boolean z) {
        this.setPercentsEnabled = z;
    }

    public void onInitializeAccessibilityNodeInfoInternal(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfoInternal(view, accessibilityNodeInfo);
        if (this.setPercentsEnabled) {
            AccessibilityNodeInfoCompat wrap = AccessibilityNodeInfoCompat.wrap(accessibilityNodeInfo);
            wrap.addAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SET_PROGRESS);
            wrap.setRangeInfo(AccessibilityNodeInfoCompat.RangeInfoCompat.obtain(1, getMinValue(), getMaxValue(), getProgress()));
        }
    }

    public boolean performAccessibilityActionInternal(View view, int i, Bundle bundle) {
        if (super.performAccessibilityActionInternal(view, i, bundle)) {
            return true;
        }
        if (i != AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SET_PROGRESS.getId()) {
            return false;
        }
        setProgress(bundle.getFloat("android.view.accessibility.action.ARGUMENT_PROGRESS_VALUE"));
        return true;
    }

    /* access modifiers changed from: protected */
    public void doScroll(View view, boolean z) {
        float delta = getDelta();
        if (z) {
            delta *= -1.0f;
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
