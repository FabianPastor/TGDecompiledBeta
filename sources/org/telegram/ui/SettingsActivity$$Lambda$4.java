package org.telegram.ui;

import org.telegram.ui.ActionBar.AlertDialog;

final /* synthetic */ class SettingsActivity$$Lambda$4 implements Runnable {
    private final SettingsActivity arg$1;
    private final AlertDialog arg$2;

    SettingsActivity$$Lambda$4(SettingsActivity settingsActivity, AlertDialog alertDialog) {
        this.arg$1 = settingsActivity;
        this.arg$2 = alertDialog;
    }

    public void run() {
        this.arg$1.lambda$sendLogs$10$SettingsActivity(this.arg$2);
    }
}
