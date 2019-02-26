package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended;

final /* synthetic */ class DataAutoDownloadActivity$$Lambda$0 implements OnItemClickListenerExtended {
    private final DataAutoDownloadActivity arg$1;

    DataAutoDownloadActivity$$Lambda$0(DataAutoDownloadActivity dataAutoDownloadActivity) {
        this.arg$1 = dataAutoDownloadActivity;
    }

    public void onItemClick(View view, int i, float f, float f2) {
        this.arg$1.lambda$createView$4$DataAutoDownloadActivity(view, i, f, f2);
    }
}
