package org.telegram.ui;

import android.view.View;
import android.view.View.OnClickListener;

final /* synthetic */ class WallpaperActivity$$Lambda$5 implements OnClickListener {
    private final WallpaperActivity arg$1;
    private final int arg$2;

    WallpaperActivity$$Lambda$5(WallpaperActivity wallpaperActivity, int i) {
        this.arg$1 = wallpaperActivity;
        this.arg$2 = i;
    }

    public void onClick(View view) {
        this.arg$1.lambda$createView$5$WallpaperActivity(this.arg$2, view);
    }
}
