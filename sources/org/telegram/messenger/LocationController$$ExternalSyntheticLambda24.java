package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class LocationController$$ExternalSyntheticLambda24 implements RequestDelegate {
    public static final /* synthetic */ LocationController$$ExternalSyntheticLambda24 INSTANCE = new LocationController$$ExternalSyntheticLambda24();

    private /* synthetic */ LocationController$$ExternalSyntheticLambda24() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        LocationController.lambda$broadcastLastKnownLocation$8(tLObject, tL_error);
    }
}
