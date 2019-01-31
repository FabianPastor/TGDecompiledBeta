package org.telegram.ui;

import org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate;

final /* synthetic */ class WallpaperActivity$$Lambda$7 implements SeekBarViewDelegate {
    private final WallpaperActivity arg$1;

    WallpaperActivity$$Lambda$7(WallpaperActivity wallpaperActivity) {
        this.arg$1 = wallpaperActivity;
    }

    public void onSeekBarDrag(float f) {
        this.arg$1.lambda$createView$7$WallpaperActivity(f);
    }
}
