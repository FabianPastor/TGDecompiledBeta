package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda124 implements RequestDelegate {
    public static final /* synthetic */ AlertsCreator$$ExternalSyntheticLambda124 INSTANCE = new AlertsCreator$$ExternalSyntheticLambda124();

    private /* synthetic */ AlertsCreator$$ExternalSyntheticLambda124() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        AlertsCreator.lambda$createChangeBioAlert$29(tLObject, tL_error);
    }
}
