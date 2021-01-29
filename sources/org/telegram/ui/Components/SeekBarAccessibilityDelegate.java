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
        public void onViewAttachedToWindow(View view) {
        }

        public void onViewDetachedFromWindow(View view) {
            view.removeCallbacks((Runnable) SeekBarAccessibilityDelegate.this.accessibilityEventRunnables.remove(view));
            view.removeOnAttachStateChangeListener(this);
        }
    };

    /* access modifiers changed from: protected */
    public abstract boolean canScrollBackward(View view);

    /* access modifiers changed from: protected */
    public abstract boolean canScrollForward(View view);

    /* access modifiers changed from: protected */
    public abstract void doScroll(View view, boolean z);

    /* access modifiers changed from: protected */
    public CharSequence getContentDescription(View view) {
        return null;
    }

    public boolean performAccessibilityAction(View view, int i, Bundle bundle) {
        if (super.performAccessibilityAction(view, i, bundle)) {
            return true;
        }
        return performAccessibilityActionInternal(view, i, bundle);
    }

    public boolean performAccessibilityActionInternal(View view, int i, Bundle bundle) {
        boolean z = false;
        if (i != 4096 && i != 8192) {
            return false;
        }
        if (i == 8192) {
            z = true;
        }
        doScroll(view, z);
        if (view != null) {
            postAccessibilityEventRunnable(view);
        }
        return true;
    }

    public final boolean performAccessibilityActionInternal(int i, Bundle bundle) {
        return performAccessibilityActionInternal((View) null, i, bundle);
    }

    private void postAccessibilityEventRunnable(View view) {
        if (ViewCompat.isAttachedToWindow(view)) {
            Runnable runnable = this.accessibilityEventRunnables.get(view);
            if (runnable == null) {
                Map<View, Runnable> map = this.accessibilityEventRunnables;
                $$Lambda$SeekBarAccessibilityDelegate$8IJe1316cK3QFfOcwBG4IAMvar_ r1 = new Runnable(view) {
                    public final /* synthetic */ View f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        SeekBarAccessibilityDelegate.this.lambda$postAccessibilityEventRunnable$0$SeekBarAccessibilityDelegate(this.f$1);
                    }
                };
                map.put(view, r1);
                view.addOnAttachStateChangeListener(this.onAttachStateChangeListener);
                runnable = r1;
            } else {
                view.removeCallbacks(runnable);
            }
            view.postDelayed(runnable, 400);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$postAccessibilityEventRunnable$0 */
    public /* synthetic */ void lambda$postAccessibilityEventRunnable$0$SeekBarAccessibilityDelegate(View view) {
        sendAccessibilityEvent(view, 4);
    }

    public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
        onInitializeAccessibilityNodeInfoInternal(view, accessibilityNodeInfo);
    }

    public void onInitializeAccessibilityNodeInfoInternal(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
        accessibilityNodeInfo.setClassName(SEEK_BAR_CLASS_NAME);
        CharSequence contentDescription = getContentDescription(view);
        if (!TextUtils.isEmpty(contentDescription)) {
            accessibilityNodeInfo.setText(contentDescription);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            if (canScrollBackward(view)) {
                accessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_BACKWARD);
            }
            if (canScrollForward(view)) {
                accessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD);
            }
        }
    }

    public final void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo accessibilityNodeInfo) {
        onInitializeAccessibilityNodeInfoInternal((View) null, accessibilityNodeInfo);
    }
}
