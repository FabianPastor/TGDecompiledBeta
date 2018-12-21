package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class LocationController$$Lambda$9 implements RequestDelegate {
    private final LocationController arg$1;
    private final long arg$2;

    LocationController$$Lambda$9(LocationController locationController, long j) {
        this.arg$1 = locationController;
        this.arg$2 = j;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$loadLiveLocations$18$LocationController(this.arg$2, tLObject, tL_error);
    }
}
