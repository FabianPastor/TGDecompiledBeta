package org.telegram.p005ui;

import org.telegram.p005ui.ActionBar.AlertDialog;

/* renamed from: org.telegram.ui.SettingsActivity$$Lambda$5 */
final /* synthetic */ class SettingsActivity$$Lambda$5 implements Runnable {
    private final SettingsActivity arg$1;
    private final AlertDialog arg$2;

    SettingsActivity$$Lambda$5(SettingsActivity settingsActivity, AlertDialog alertDialog) {
        this.arg$1 = settingsActivity;
        this.arg$2 = alertDialog;
    }

    public void run() {
        this.arg$1.lambda$sendLogs$14$SettingsActivity(this.arg$2);
    }
}
