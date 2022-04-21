package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda1 implements RequestDelegate {
    public static final /* synthetic */ AlertsCreator$$ExternalSyntheticLambda1 INSTANCE = new AlertsCreator$$ExternalSyntheticLambda1();

    private /* synthetic */ AlertsCreator$$ExternalSyntheticLambda1() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        AlertsCreator.lambda$sendReport$83(tLObject, tL_error);
    }
}
