package org.telegram.p005ui;

import android.view.View;
import org.telegram.p005ui.Components.RecyclerListView.OnItemClickListener;

/* renamed from: org.telegram.ui.WallpapersActivity$$Lambda$1 */
final /* synthetic */ class WallpapersActivity$$Lambda$1 implements OnItemClickListener {
    private final WallpapersActivity arg$1;

    WallpapersActivity$$Lambda$1(WallpapersActivity wallpapersActivity) {
        this.arg$1 = wallpapersActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$1$WallpapersActivity(view, i);
    }
}
