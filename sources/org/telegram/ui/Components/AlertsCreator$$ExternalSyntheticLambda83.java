package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda83 implements RequestDelegate {
    public static final /* synthetic */ AlertsCreator$$ExternalSyntheticLambda83 INSTANCE = new AlertsCreator$$ExternalSyntheticLambda83();

    private /* synthetic */ AlertsCreator$$ExternalSyntheticLambda83() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AlertsCreator.lambda$createChangeNameAlert$26(tLObject, tLRPC$TL_error);
    }
}
