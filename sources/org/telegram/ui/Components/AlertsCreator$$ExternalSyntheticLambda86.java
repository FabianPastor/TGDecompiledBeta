package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda86 implements RequestDelegate {
    public static final /* synthetic */ AlertsCreator$$ExternalSyntheticLambda86 INSTANCE = new AlertsCreator$$ExternalSyntheticLambda86();

    private /* synthetic */ AlertsCreator$$ExternalSyntheticLambda86() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        AlertsCreator.lambda$createChangeNameAlert$26(tLObject, tL_error);
    }
}
