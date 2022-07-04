package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda125 implements RequestDelegate {
    public static final /* synthetic */ AlertsCreator$$ExternalSyntheticLambda125 INSTANCE = new AlertsCreator$$ExternalSyntheticLambda125();

    private /* synthetic */ AlertsCreator$$ExternalSyntheticLambda125() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        AlertsCreator.lambda$createChangeNameAlert$33(tLObject, tL_error);
    }
}
