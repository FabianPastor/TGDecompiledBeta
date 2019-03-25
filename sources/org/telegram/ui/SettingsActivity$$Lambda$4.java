package org.telegram.ui;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.ui.ActionBar.BottomSheet.Builder;

final /* synthetic */ class SettingsActivity$$Lambda$4 implements OnClickListener {
    private final SettingsActivity arg$1;
    private final Builder arg$2;

    SettingsActivity$$Lambda$4(SettingsActivity settingsActivity, Builder builder) {
        this.arg$1 = settingsActivity;
        this.arg$2 = builder;
    }

    public void onClick(View view) {
        this.arg$1.lambda$showHelpAlert$8$SettingsActivity(this.arg$2, view);
    }
}
