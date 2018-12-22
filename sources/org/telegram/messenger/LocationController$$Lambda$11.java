package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class LocationController$$Lambda$11 implements RequestDelegate {
    private final LocationController arg$1;

    LocationController$$Lambda$11(LocationController locationController) {
        this.arg$1 = locationController;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$14$LocationController(tLObject, tL_error);
    }
}
