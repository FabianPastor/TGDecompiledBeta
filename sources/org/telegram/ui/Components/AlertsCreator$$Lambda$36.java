package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class AlertsCreator$$Lambda$36 implements RequestDelegate {
    static final RequestDelegate $instance = new AlertsCreator$$Lambda$36();

    private AlertsCreator$$Lambda$36() {
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        AlertsCreator.lambda$null$18$AlertsCreator(tLObject, tL_error);
    }
}
