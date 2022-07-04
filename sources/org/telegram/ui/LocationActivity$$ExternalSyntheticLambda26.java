package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class LocationActivity$$ExternalSyntheticLambda26 implements RequestDelegate {
    public final /* synthetic */ LocationActivity f$0;
    public final /* synthetic */ long f$1;

    public /* synthetic */ LocationActivity$$ExternalSyntheticLambda26(LocationActivity locationActivity, long j) {
        this.f$0 = locationActivity;
        this.f$1 = j;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$getRecentLocations$36(this.f$1, tLObject, tLRPC$TL_error);
    }
}
