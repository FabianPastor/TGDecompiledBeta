package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class AlertsCreator$$Lambda$46 implements RequestDelegate {
    static final RequestDelegate $instance = new AlertsCreator$$Lambda$46();

    private AlertsCreator$$Lambda$46() {
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        AlertsCreator.lambda$null$19$AlertsCreator(tLObject, tL_error);
    }
}
