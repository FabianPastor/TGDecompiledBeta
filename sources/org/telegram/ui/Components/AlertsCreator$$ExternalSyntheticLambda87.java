package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda87 implements RequestDelegate {
    public static final /* synthetic */ AlertsCreator$$ExternalSyntheticLambda87 INSTANCE = new AlertsCreator$$ExternalSyntheticLambda87();

    private /* synthetic */ AlertsCreator$$ExternalSyntheticLambda87() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        AlertsCreator.lambda$createDeleteMessagesAlert$95(tLObject, tL_error);
    }
}
