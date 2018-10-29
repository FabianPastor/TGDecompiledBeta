package org.telegram.ui;

import android.content.SharedPreferences;
import org.telegram.tgnet.TLRPC.TL_help_support;
import org.telegram.ui.ActionBar.AlertDialog;

final /* synthetic */ class SettingsActivity$$Lambda$4 implements Runnable {
    private final SettingsActivity arg$1;
    private final SharedPreferences arg$2;
    private final TL_help_support arg$3;
    private final AlertDialog arg$4;

    SettingsActivity$$Lambda$4(SettingsActivity settingsActivity, SharedPreferences sharedPreferences, TL_help_support tL_help_support, AlertDialog alertDialog) {
        this.arg$1 = settingsActivity;
        this.arg$2 = sharedPreferences;
        this.arg$3 = tL_help_support;
        this.arg$4 = alertDialog;
    }

    public void run() {
        this.arg$1.lambda$null$6$SettingsActivity(this.arg$2, this.arg$3, this.arg$4);
    }
}
