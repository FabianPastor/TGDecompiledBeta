package org.telegram.ui;

final /* synthetic */ class SettingsActivity$SearchAdapter$$Lambda$76 implements Runnable {
    private final SettingsActivity arg$1;

    SettingsActivity$SearchAdapter$$Lambda$76(SettingsActivity settingsActivity) {
        this.arg$1 = settingsActivity;
    }

    public void run() {
        this.arg$1.showHelpAlert();
    }
}
