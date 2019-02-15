package org.telegram.ui;

import android.view.View;
import android.view.View.OnClickListener;

final /* synthetic */ class WallpaperActivity$$Lambda$2 implements OnClickListener {
    private final WallpaperActivity arg$1;
    private final int arg$2;
    private final CheckBoxView arg$3;

    WallpaperActivity$$Lambda$2(WallpaperActivity wallpaperActivity, int i, CheckBoxView checkBoxView) {
        this.arg$1 = wallpaperActivity;
        this.arg$2 = i;
        this.arg$3 = checkBoxView;
    }

    public void onClick(View view) {
        this.arg$1.lambda$createView$2$WallpaperActivity(this.arg$2, this.arg$3, view);
    }
}
