package org.telegram.p005ui;

import org.telegram.p005ui.Cells.TextCheckCell;

/* renamed from: org.telegram.ui.PrivacySettingsActivity$$Lambda$14 */
final /* synthetic */ class PrivacySettingsActivity$$Lambda$14 implements Runnable {
    private final PrivacySettingsActivity arg$1;
    private final TextCheckCell arg$2;

    PrivacySettingsActivity$$Lambda$14(PrivacySettingsActivity privacySettingsActivity, TextCheckCell textCheckCell) {
        this.arg$1 = privacySettingsActivity;
        this.arg$2 = textCheckCell;
    }

    public void run() {
        this.arg$1.lambda$null$9$PrivacySettingsActivity(this.arg$2);
    }
}
