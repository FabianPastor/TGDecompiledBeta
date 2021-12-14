package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class LocationController$$ExternalSyntheticLambda31 implements RequestDelegate {
    public static final /* synthetic */ LocationController$$ExternalSyntheticLambda31 INSTANCE = new LocationController$$ExternalSyntheticLambda31();

    private /* synthetic */ LocationController$$ExternalSyntheticLambda31() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        LocationController.lambda$broadcastLastKnownLocation$8(tLObject, tLRPC$TL_error);
    }
}
