package org.telegram.ui;

import android.view.View;
import android.view.View.OnClickListener;

final /* synthetic */ class WallpaperActivity$$Lambda$2 implements OnClickListener {
    private final WallpaperActivity arg$1;
    private final CheckBoxView arg$2;
    private final int arg$3;

    WallpaperActivity$$Lambda$2(WallpaperActivity wallpaperActivity, CheckBoxView checkBoxView, int i) {
        this.arg$1 = wallpaperActivity;
        this.arg$2 = checkBoxView;
        this.arg$3 = i;
    }

    public void onClick(View view) {
        this.arg$1.lambda$createView$3$WallpaperActivity(this.arg$2, this.arg$3, view);
    }
}
