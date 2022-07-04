package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class LocationController$$ExternalSyntheticLambda21 implements RequestDelegate {
    public final /* synthetic */ LocationController f$0;
    public final /* synthetic */ long f$1;

    public /* synthetic */ LocationController$$ExternalSyntheticLambda21(LocationController locationController, long j) {
        this.f$0 = locationController;
        this.f$1 = j;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m1922x22591025(this.f$1, tLObject, tL_error);
    }
}
