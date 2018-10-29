package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class AlertsCreator$$Lambda$31 implements RequestDelegate {
    static final RequestDelegate $instance = new AlertsCreator$$Lambda$31();

    private AlertsCreator$$Lambda$31() {
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        AlertsCreator.lambda$null$11$AlertsCreator(tLObject, tL_error);
    }
}
