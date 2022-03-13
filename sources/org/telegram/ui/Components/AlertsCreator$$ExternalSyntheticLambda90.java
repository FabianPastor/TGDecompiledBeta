package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda90 implements RequestDelegate {
    public static final /* synthetic */ AlertsCreator$$ExternalSyntheticLambda90 INSTANCE = new AlertsCreator$$ExternalSyntheticLambda90();

    private /* synthetic */ AlertsCreator$$ExternalSyntheticLambda90() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AlertsCreator.lambda$createChangeBioAlert$26(tLObject, tLRPC$TL_error);
    }
}
