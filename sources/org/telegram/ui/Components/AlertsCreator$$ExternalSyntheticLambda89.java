package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda89 implements RequestDelegate {
    public static final /* synthetic */ AlertsCreator$$ExternalSyntheticLambda89 INSTANCE = new AlertsCreator$$ExternalSyntheticLambda89();

    private /* synthetic */ AlertsCreator$$ExternalSyntheticLambda89() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AlertsCreator.lambda$sendReport$66(tLObject, tLRPC$TL_error);
    }
}
