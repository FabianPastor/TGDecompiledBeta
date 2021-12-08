package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda82 implements RequestDelegate {
    public static final /* synthetic */ AlertsCreator$$ExternalSyntheticLambda82 INSTANCE = new AlertsCreator$$ExternalSyntheticLambda82();

    private /* synthetic */ AlertsCreator$$ExternalSyntheticLambda82() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AlertsCreator.lambda$sendReport$62(tLObject, tLRPC$TL_error);
    }
}
