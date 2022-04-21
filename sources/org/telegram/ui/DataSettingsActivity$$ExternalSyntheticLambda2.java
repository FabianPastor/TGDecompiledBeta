package org.telegram.ui;

import android.content.DialogInterface;
import android.content.SharedPreferences;

public final /* synthetic */ class DataSettingsActivity$$ExternalSyntheticLambda2 implements DialogInterface.OnClickListener {
    public final /* synthetic */ DataSettingsActivity f$0;
    public final /* synthetic */ SharedPreferences f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ DataSettingsActivity$$ExternalSyntheticLambda2(DataSettingsActivity dataSettingsActivity, SharedPreferences sharedPreferences, int i) {
        this.f$0 = dataSettingsActivity;
        this.f$1 = sharedPreferences;
        this.f$2 = i;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m2057lambda$createView$1$orgtelegramuiDataSettingsActivity(this.f$1, this.f$2, dialogInterface, i);
    }
}
