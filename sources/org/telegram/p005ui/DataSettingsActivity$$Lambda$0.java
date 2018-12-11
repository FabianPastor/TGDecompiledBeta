package org.telegram.p005ui;

import android.view.View;
import org.telegram.p005ui.Components.RecyclerListView.OnItemClickListener;

/* renamed from: org.telegram.ui.DataSettingsActivity$$Lambda$0 */
final /* synthetic */ class DataSettingsActivity$$Lambda$0 implements OnItemClickListener {
    private final DataSettingsActivity arg$1;

    DataSettingsActivity$$Lambda$0(DataSettingsActivity dataSettingsActivity) {
        this.arg$1 = dataSettingsActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$2$DataSettingsActivity(view, i);
    }
}
