package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

final /* synthetic */ class DataUsageActivity$$Lambda$0 implements OnItemClickListener {
    private final DataUsageActivity arg$1;
    private final RecyclerListView arg$2;

    DataUsageActivity$$Lambda$0(DataUsageActivity dataUsageActivity, RecyclerListView recyclerListView) {
        this.arg$1 = dataUsageActivity;
        this.arg$2 = recyclerListView;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$2$DataUsageActivity(this.arg$2, view, i);
    }
}
