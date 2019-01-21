package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class LocationActivity$$Lambda$10 implements RequestDelegate {
    private final LocationActivity arg$1;
    private final long arg$2;

    LocationActivity$$Lambda$10(LocationActivity locationActivity, long j) {
        this.arg$1 = locationActivity;
        this.arg$2 = j;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$getRecentLocations$14$LocationActivity(this.arg$2, tLObject, tL_error);
    }
}
