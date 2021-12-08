package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda85 implements RequestDelegate {
    public static final /* synthetic */ AlertsCreator$$ExternalSyntheticLambda85 INSTANCE = new AlertsCreator$$ExternalSyntheticLambda85();

    private /* synthetic */ AlertsCreator$$ExternalSyntheticLambda85() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        AlertsCreator.lambda$createChangeBioAlert$22(tLObject, tL_error);
    }
}
