package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.Cells.TextCheckCell;

final /* synthetic */ class PrivacySettingsActivity$$Lambda$14 implements RequestDelegate {
    private final PrivacySettingsActivity arg$1;
    private final TextCheckCell arg$2;

    PrivacySettingsActivity$$Lambda$14(PrivacySettingsActivity privacySettingsActivity, TextCheckCell textCheckCell) {
        this.arg$1 = privacySettingsActivity;
        this.arg$2 = textCheckCell;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$10$PrivacySettingsActivity(this.arg$2, tLObject, tL_error);
    }
}
