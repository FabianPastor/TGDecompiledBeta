package org.telegram.ui;

import org.telegram.ui.Components.WallpaperParallaxEffect.Callback;

final /* synthetic */ class WallpaperActivity$$Lambda$3 implements Callback {
    private final WallpaperActivity arg$1;

    WallpaperActivity$$Lambda$3(WallpaperActivity wallpaperActivity) {
        this.arg$1 = wallpaperActivity;
    }

    public void onOffsetsChanged(int i, int i2) {
        this.arg$1.lambda$createView$4$WallpaperActivity(i, i2);
    }
}
