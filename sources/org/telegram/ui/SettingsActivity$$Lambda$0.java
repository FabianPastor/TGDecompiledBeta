package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

final /* synthetic */ class SettingsActivity$$Lambda$0 implements OnItemClickListener {
    private final SettingsActivity arg$1;

    SettingsActivity$$Lambda$0(SettingsActivity settingsActivity) {
        this.arg$1 = settingsActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$0$SettingsActivity(view, i);
    }
}
