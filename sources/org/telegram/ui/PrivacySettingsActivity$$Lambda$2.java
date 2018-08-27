package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class PrivacySettingsActivity$$Lambda$2 implements RequestDelegate {
    private final PrivacySettingsActivity arg$1;

    PrivacySettingsActivity$$Lambda$2(PrivacySettingsActivity privacySettingsActivity) {
        this.arg$1 = privacySettingsActivity;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$loadPasswordSettings$20$PrivacySettingsActivity(tLObject, tL_error);
    }
}
