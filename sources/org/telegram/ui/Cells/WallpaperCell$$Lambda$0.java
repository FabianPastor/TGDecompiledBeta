package org.telegram.ui.Cells;

import android.view.View;
import android.view.View.OnClickListener;

final /* synthetic */ class WallpaperCell$$Lambda$0 implements OnClickListener {
    private final WallpaperCell arg$1;
    private final WallpaperView arg$2;

    WallpaperCell$$Lambda$0(WallpaperCell wallpaperCell, WallpaperView wallpaperView) {
        this.arg$1 = wallpaperCell;
        this.arg$2 = wallpaperView;
    }

    public void onClick(View view) {
        this.arg$1.lambda$new$0$WallpaperCell(this.arg$2, view);
    }
}
