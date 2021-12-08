package org.telegram.ui.Components;

public final /* synthetic */ class SizeNotifierFrameLayout$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ SizeNotifierFrameLayout f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ SizeNotifierFrameLayout$$ExternalSyntheticLambda0(SizeNotifierFrameLayout sizeNotifierFrameLayout, boolean z) {
        this.f$0 = sizeNotifierFrameLayout;
        this.f$1 = z;
    }

    public final void run() {
        this.f$0.lambda$notifyHeightChanged$1(this.f$1);
    }
}
