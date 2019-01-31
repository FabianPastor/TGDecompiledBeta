package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

final /* synthetic */ class WallpapersListActivity$SearchAdapter$$Lambda$3 implements OnItemClickListener {
    private final SearchAdapter arg$1;

    WallpapersListActivity$SearchAdapter$$Lambda$3(SearchAdapter searchAdapter) {
        this.arg$1 = searchAdapter;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$onCreateViewHolder$5$WallpapersListActivity$SearchAdapter(view, i);
    }
}
