package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda127 implements RequestDelegate {
    public static final /* synthetic */ AlertsCreator$$ExternalSyntheticLambda127 INSTANCE = new AlertsCreator$$ExternalSyntheticLambda127();

    private /* synthetic */ AlertsCreator$$ExternalSyntheticLambda127() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        AlertsCreator.lambda$createReportAlert$85(tLObject, tL_error);
    }
}
