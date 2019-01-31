package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

final /* synthetic */ class WallpapersListActivity$$Lambda$1 implements OnItemClickListener {
    private final WallpapersListActivity arg$1;

    WallpapersListActivity$$Lambda$1(WallpapersListActivity wallpapersListActivity) {
        this.arg$1 = wallpapersListActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$3$WallpapersListActivity(view, i);
    }
}
