package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;

final /* synthetic */ class DataSettingsActivity$$Lambda$2 implements OnClickListener {
    private final DataSettingsActivity arg$1;
    private final SharedPreferences arg$2;
    private final int arg$3;

    DataSettingsActivity$$Lambda$2(DataSettingsActivity dataSettingsActivity, SharedPreferences sharedPreferences, int i) {
        this.arg$1 = dataSettingsActivity;
        this.arg$2 = sharedPreferences;
        this.arg$3 = i;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$1$DataSettingsActivity(this.arg$2, this.arg$3, dialogInterface, i);
    }
}
