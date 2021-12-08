package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda88 implements RequestDelegate {
    public static final /* synthetic */ AlertsCreator$$ExternalSyntheticLambda88 INSTANCE = new AlertsCreator$$ExternalSyntheticLambda88();

    private /* synthetic */ AlertsCreator$$ExternalSyntheticLambda88() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        AlertsCreator.lambda$createReportAlert$63(tLObject, tL_error);
    }
}
