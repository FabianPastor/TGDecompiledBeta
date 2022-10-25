package org.telegram.ui.Components.FloatingDebug;

import android.annotation.SuppressLint;
import android.widget.FrameLayout;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.LaunchActivity;
/* loaded from: classes3.dex */
public class FloatingDebugController {
    private static FloatingDebugView debugView;

    /* loaded from: classes3.dex */
    public enum DebugItemType {
        SIMPLE
    }

    public static boolean isActive() {
        return SharedConfig.isFloatingDebugActive;
    }

    public static boolean onBackPressed() {
        FloatingDebugView floatingDebugView = debugView;
        return floatingDebugView != null && floatingDebugView.onBackPressed();
    }

    public static void onDestroy() {
        FloatingDebugView floatingDebugView = debugView;
        if (floatingDebugView != null) {
            floatingDebugView.saveConfig();
        }
        debugView = null;
    }

    public static void setActive(LaunchActivity launchActivity, boolean z) {
        setActive(launchActivity, z, true);
    }

    @SuppressLint({"WrongConstant"})
    public static void setActive(final LaunchActivity launchActivity, boolean z, boolean z2) {
        FloatingDebugView floatingDebugView = debugView;
        if (z == (floatingDebugView != null)) {
            return;
        }
        if (z) {
            debugView = new FloatingDebugView(launchActivity);
            launchActivity.getMainContainerFrameLayout().addView(debugView, new FrameLayout.LayoutParams(-1, -1));
            debugView.showFab();
        } else {
            floatingDebugView.dismiss(new Runnable() { // from class: org.telegram.ui.Components.FloatingDebug.FloatingDebugController$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    FloatingDebugController.lambda$setActive$0(LaunchActivity.this);
                }
            });
        }
        if (!z2) {
            return;
        }
        SharedConfig.isFloatingDebugActive = z;
        SharedConfig.saveConfig();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$setActive$0(LaunchActivity launchActivity) {
        launchActivity.getMainContainerFrameLayout().removeView(debugView);
        debugView = null;
    }

    /* loaded from: classes3.dex */
    public static class DebugItem {
        final Runnable action;
        final CharSequence title;
        final DebugItemType type = DebugItemType.SIMPLE;

        public DebugItem(CharSequence charSequence, Runnable runnable) {
            this.title = charSequence;
            this.action = runnable;
        }
    }
}
