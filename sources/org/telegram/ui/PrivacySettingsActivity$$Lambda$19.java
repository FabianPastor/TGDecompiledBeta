package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class PrivacySettingsActivity$$Lambda$19 implements RequestDelegate {
    private final PrivacySettingsActivity arg$1;

    PrivacySettingsActivity$$Lambda$19(PrivacySettingsActivity privacySettingsActivity) {
        this.arg$1 = privacySettingsActivity;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$2$PrivacySettingsActivity(tLObject, tL_error);
    }
}
