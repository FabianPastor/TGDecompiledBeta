package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class AlertsCreator$$Lambda$43 implements RequestDelegate {
    static final RequestDelegate $instance = new AlertsCreator$$Lambda$43();

    private AlertsCreator$$Lambda$43() {
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        AlertsCreator.lambda$null$47$AlertsCreator(tLObject, tL_error);
    }
}
