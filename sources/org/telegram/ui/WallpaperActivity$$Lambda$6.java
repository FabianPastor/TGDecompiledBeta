package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

final /* synthetic */ class WallpaperActivity$$Lambda$6 implements OnItemClickListener {
    private final WallpaperActivity arg$1;

    WallpaperActivity$$Lambda$6(WallpaperActivity wallpaperActivity) {
        this.arg$1 = wallpaperActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$6$WallpaperActivity(view, i);
    }
}
