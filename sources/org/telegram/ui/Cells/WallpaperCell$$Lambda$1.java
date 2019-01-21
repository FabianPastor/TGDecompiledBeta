package org.telegram.ui.Cells;

import android.view.View;
import android.view.View.OnLongClickListener;

final /* synthetic */ class WallpaperCell$$Lambda$1 implements OnLongClickListener {
    private final WallpaperCell arg$1;
    private final WallpaperView arg$2;

    WallpaperCell$$Lambda$1(WallpaperCell wallpaperCell, WallpaperView wallpaperView) {
        this.arg$1 = wallpaperCell;
        this.arg$2 = wallpaperView;
    }

    public boolean onLongClick(View view) {
        return this.arg$1.lambda$new$1$WallpaperCell(this.arg$2, view);
    }
}
