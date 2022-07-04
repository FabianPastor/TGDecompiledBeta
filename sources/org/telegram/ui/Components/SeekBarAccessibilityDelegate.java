package org.telegram.ui.Components;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.SeekBar;
import androidx.core.view.ViewCompat;
import java.util.HashMap;
import java.util.Map;

public abstract class SeekBarAccessibilityDelegate extends View.AccessibilityDelegate {
    private static final CharSequence SEEK_BAR_CLASS_NAME = SeekBar.class.getName();
    /* access modifiers changed from: private */
    public final Map<View, Runnable> accessibilityEventRunnables = new HashMap(4);
    private final View.OnAttachStateChangeListener onAttachStateChangeListener = new View.OnAttachStateChangeListener() {
        public void onViewAttachedToWindow(View v) {
        }

        public void onViewDetachedFromWindow(View v) {
            v.removeCallbacks((Runnable) SeekBarAccessibilityDelegate.this.accessibilityEventRunnables.remove(v));
            v.removeOnAttachStateChangeListener(this);
        }
    };

    /* access modifiers changed from: protected */
    public abstract boolean canScrollBackward(View view);

    /* access modifiers changed from: protected */
    public abstract boolean canScrollForward(View view);

    /* access modifiers changed from: protected */
    public abstract void doScroll(View view, boolean z);

    public boolean performAccessibilityAction(View host, int action, Bundle args) {
        if (super.performAccessibilityAction(host, action, args)) {
            return true;
        }
        return performAccessibilityActionInternal(host, action, args);
    }

    public boolean performAccessibilityActionInternal(View host, int action, Bundle args) {
        boolean z = false;
        if (action != 4096 && action != 8192) {
            return false;
        }
        if (action == 8192) {
            z = true;
        }
        doScroll(host, z);
        if (host != null) {
            postAccessibilityEventRunnable(host);
        }
        return true;
    }

    public final boolean performAccessibilityActionInternal(int action, Bundle args) {
        return performAccessibilityActionInternal((View) null, action, args);
    }

    private void postAccessibilityEventRunnable(View host) {
        if (ViewCompat.isAttachedToWindow(host)) {
            Runnable runnable = this.accessibilityEventRunnables.get(host);
            if (runnable == null) {
                Map<View, Runnable> map = this.accessibilityEventRunnables;
                SeekBarAccessibilityDelegate$$ExternalSyntheticLambda0 seekBarAccessibilityDelegate$$ExternalSyntheticLambda0 = new SeekBarAccessibilityDelegate$$ExternalSyntheticLambda0(this, host);
                runnable = seekBarAccessibilityDelegate$$ExternalSyntheticLambda0;
                map.put(host, seekBarAccessibilityDelegate$$ExternalSyntheticLambda0);
                host.addOnAttachStateChangeListener(this.onAttachStateChangeListener);
            } else {
                host.removeCallbacks(runnable);
            }
            host.postDelayed(runnable, 400);
        }
    }

    /* renamed from: lambda$postAccessibilityEventRunnable$0$org-telegram-ui-Components-SeekBarAccessibilityDelegate  reason: not valid java name */
    public /* synthetic */ void m1335x308b6507(View host) {
        sendAccessibilityEvent(host, 4);
    }

    public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(host, info);
        onInitializeAccessibilityNodeInfoInternal(host, info);
    }

    public void onInitializeAccessibilityNodeInfoInternal(View host, AccessibilityNodeInfo info) {
        info.setClassName(SEEK_BAR_CLASS_NAME);
        CharSequence contentDescription = getContentDescription(host);
        if (!TextUtils.isEmpty(contentDescription)) {
            info.setText(contentDescription);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            if (canScrollBackward(host)) {
                info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_BACKWARD);
            }
            if (canScrollForward(host)) {
                info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD);
            }
        }
    }

    public final void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo info) {
        onInitializeAccessibilityNodeInfoInternal((View) null, info);
    }

    /* access modifiers changed from: protected */
    public CharSequence getContentDescription(View host) {
        return null;
    }
}
