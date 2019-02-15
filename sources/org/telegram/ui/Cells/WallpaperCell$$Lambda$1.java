package org.telegram.ui.Cells;

import android.view.View;
import android.view.View.OnLongClickListener;

final /* synthetic */ class WallpaperCell$$Lambda$1 implements OnLongClickListener {
    private final WallpaperCell arg$1;
    private final WallpaperView arg$2;
    private final int arg$3;

    WallpaperCell$$Lambda$1(WallpaperCell wallpaperCell, WallpaperView wallpaperView, int i) {
        this.arg$1 = wallpaperCell;
        this.arg$2 = wallpaperView;
        this.arg$3 = i;
    }

    public boolean onLongClick(View view) {
        return this.arg$1.lambda$new$1$WallpaperCell(this.arg$2, this.arg$3, view);
    }
}
