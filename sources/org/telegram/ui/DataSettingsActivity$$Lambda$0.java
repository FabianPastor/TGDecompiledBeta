package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

final /* synthetic */ class DataSettingsActivity$$Lambda$0 implements OnItemClickListener {
    private final DataSettingsActivity arg$1;

    DataSettingsActivity$$Lambda$0(DataSettingsActivity dataSettingsActivity) {
        this.arg$1 = dataSettingsActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$2$DataSettingsActivity(view, i);
    }
}
