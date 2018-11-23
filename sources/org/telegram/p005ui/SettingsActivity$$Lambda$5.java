package org.telegram.p005ui;

import android.content.SharedPreferences;
import org.telegram.p005ui.ActionBar.AlertDialog;
import org.telegram.tgnet.TLRPC.TL_help_support;

/* renamed from: org.telegram.ui.SettingsActivity$$Lambda$5 */
final /* synthetic */ class SettingsActivity$$Lambda$5 implements Runnable {
    private final SettingsActivity arg$1;
    private final SharedPreferences arg$2;
    private final TL_help_support arg$3;
    private final AlertDialog arg$4;

    SettingsActivity$$Lambda$5(SettingsActivity settingsActivity, SharedPreferences sharedPreferences, TL_help_support tL_help_support, AlertDialog alertDialog) {
        this.arg$1 = settingsActivity;
        this.arg$2 = sharedPreferences;
        this.arg$3 = tL_help_support;
        this.arg$4 = alertDialog;
    }

    public void run() {
        this.arg$1.lambda$null$10$SettingsActivity(this.arg$2, this.arg$3, this.arg$4);
    }
}
