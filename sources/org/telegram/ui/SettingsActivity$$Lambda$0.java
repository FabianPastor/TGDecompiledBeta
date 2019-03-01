package org.telegram.ui;

import android.content.Context;
import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

final /* synthetic */ class SettingsActivity$$Lambda$0 implements OnItemClickListener {
    private final SettingsActivity arg$1;
    private final Context arg$2;

    SettingsActivity$$Lambda$0(SettingsActivity settingsActivity, Context context) {
        this.arg$1 = settingsActivity;
        this.arg$2 = context;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$2$SettingsActivity(this.arg$2, view, i);
    }
}
