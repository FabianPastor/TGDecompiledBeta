package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended;

final /* synthetic */ class DataSettingsActivity$$Lambda$0 implements OnItemClickListenerExtended {
    private final DataSettingsActivity arg$1;

    DataSettingsActivity$$Lambda$0(DataSettingsActivity dataSettingsActivity) {
        this.arg$1 = dataSettingsActivity;
    }

    public void onItemClick(View view, int i, float f, float f2) {
        this.arg$1.lambda$createView$2$DataSettingsActivity(view, i, f, f2);
    }
}
