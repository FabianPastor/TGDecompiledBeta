package org.telegram.p005ui;

import android.content.SharedPreferences;
import org.telegram.p005ui.ActionBar.AlertDialog;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.SettingsActivity$$Lambda$4 */
final /* synthetic */ class SettingsActivity$$Lambda$4 implements RequestDelegate {
    private final SettingsActivity arg$1;
    private final SharedPreferences arg$2;
    private final AlertDialog arg$3;

    SettingsActivity$$Lambda$4(SettingsActivity settingsActivity, SharedPreferences sharedPreferences, AlertDialog alertDialog) {
        this.arg$1 = settingsActivity;
        this.arg$2 = sharedPreferences;
        this.arg$3 = alertDialog;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$performAskAQuestion$12$SettingsActivity(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
