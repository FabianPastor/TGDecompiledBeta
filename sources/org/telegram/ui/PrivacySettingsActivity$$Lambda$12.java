package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class PrivacySettingsActivity$$Lambda$12 implements RequestDelegate {
    static final RequestDelegate $instance = new PrivacySettingsActivity$$Lambda$12();

    private PrivacySettingsActivity$$Lambda$12() {
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        PrivacySettingsActivity.lambda$null$14$PrivacySettingsActivity(tLObject, tL_error);
    }
}
