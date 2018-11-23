package org.telegram.p005ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.SettingsActivity$$Lambda$11 */
final /* synthetic */ class SettingsActivity$$Lambda$11 implements RequestDelegate {
    private final SettingsActivity arg$1;

    SettingsActivity$$Lambda$11(SettingsActivity settingsActivity) {
        this.arg$1 = settingsActivity;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$1$SettingsActivity(tLObject, tL_error);
    }
}
