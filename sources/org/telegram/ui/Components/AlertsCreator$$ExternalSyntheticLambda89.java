package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda89 implements RequestDelegate {
    public static final /* synthetic */ AlertsCreator$$ExternalSyntheticLambda89 INSTANCE = new AlertsCreator$$ExternalSyntheticLambda89();

    private /* synthetic */ AlertsCreator$$ExternalSyntheticLambda89() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        AlertsCreator.lambda$sendReport$62(tLObject, tL_error);
    }
}
