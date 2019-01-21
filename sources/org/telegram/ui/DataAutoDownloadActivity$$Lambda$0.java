package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

final /* synthetic */ class DataAutoDownloadActivity$$Lambda$0 implements OnItemClickListener {
    private final DataAutoDownloadActivity arg$1;

    DataAutoDownloadActivity$$Lambda$0(DataAutoDownloadActivity dataAutoDownloadActivity) {
        this.arg$1 = dataAutoDownloadActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$0$DataAutoDownloadActivity(view, i);
    }
}
