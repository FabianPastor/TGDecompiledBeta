package org.telegram.ui;

import java.io.File;
import org.telegram.ui.ActionBar.AlertDialog;

final /* synthetic */ class SettingsActivity$$Lambda$6 implements Runnable {
    private final SettingsActivity arg$1;
    private final AlertDialog arg$2;
    private final boolean[] arg$3;
    private final File arg$4;

    SettingsActivity$$Lambda$6(SettingsActivity settingsActivity, AlertDialog alertDialog, boolean[] zArr, File file) {
        this.arg$1 = settingsActivity;
        this.arg$2 = alertDialog;
        this.arg$3 = zArr;
        this.arg$4 = file;
    }

    public void run() {
        this.arg$1.lambda$null$13$SettingsActivity(this.arg$2, this.arg$3, this.arg$4);
    }
}
