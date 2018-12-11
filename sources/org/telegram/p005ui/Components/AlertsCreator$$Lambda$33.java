package org.telegram.p005ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.Components.AlertsCreator$$Lambda$33 */
final /* synthetic */ class AlertsCreator$$Lambda$33 implements RequestDelegate {
    static final RequestDelegate $instance = new AlertsCreator$$Lambda$33();

    private AlertsCreator$$Lambda$33() {
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        AlertsCreator.lambda$null$13$AlertsCreator(tLObject, tL_error);
    }
}
