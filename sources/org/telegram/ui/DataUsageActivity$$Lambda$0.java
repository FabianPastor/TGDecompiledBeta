package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

final /* synthetic */ class DataUsageActivity$$Lambda$0 implements OnItemClickListener {
    private final DataUsageActivity arg$1;

    DataUsageActivity$$Lambda$0(DataUsageActivity dataUsageActivity) {
        this.arg$1 = dataUsageActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$1$DataUsageActivity(view, i);
    }
}
