package org.telegram.ui.Components;

final /* synthetic */ class SizeNotifierFrameLayout$$Lambda$1 implements Runnable {
    private final SizeNotifierFrameLayout arg$1;
    private final boolean arg$2;

    SizeNotifierFrameLayout$$Lambda$1(SizeNotifierFrameLayout sizeNotifierFrameLayout, boolean z) {
        this.arg$1 = sizeNotifierFrameLayout;
        this.arg$2 = z;
    }

    public void run() {
        this.arg$1.lambda$notifyHeightChanged$1$SizeNotifierFrameLayout(this.arg$2);
    }
}
