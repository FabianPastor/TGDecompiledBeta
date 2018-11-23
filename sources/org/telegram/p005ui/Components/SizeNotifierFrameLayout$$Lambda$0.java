package org.telegram.p005ui.Components;

/* renamed from: org.telegram.ui.Components.SizeNotifierFrameLayout$$Lambda$0 */
final /* synthetic */ class SizeNotifierFrameLayout$$Lambda$0 implements Runnable {
    private final SizeNotifierFrameLayout arg$1;
    private final boolean arg$2;

    SizeNotifierFrameLayout$$Lambda$0(SizeNotifierFrameLayout sizeNotifierFrameLayout, boolean z) {
        this.arg$1 = sizeNotifierFrameLayout;
        this.arg$2 = z;
    }

    public void run() {
        this.arg$1.lambda$notifyHeightChanged$0$SizeNotifierFrameLayout(this.arg$2);
    }
}
