package org.telegram.ui.Components;

import org.telegram.ui.Components.WallpaperParallaxEffect.Callback;

final /* synthetic */ class SizeNotifierFrameLayout$$Lambda$0 implements Callback {
    private final SizeNotifierFrameLayout arg$1;

    SizeNotifierFrameLayout$$Lambda$0(SizeNotifierFrameLayout sizeNotifierFrameLayout) {
        this.arg$1 = sizeNotifierFrameLayout;
    }

    public void onOffsetsChanged(int i, int i2) {
        this.arg$1.lambda$setBackgroundImage$0$SizeNotifierFrameLayout(i, i2);
    }
}
