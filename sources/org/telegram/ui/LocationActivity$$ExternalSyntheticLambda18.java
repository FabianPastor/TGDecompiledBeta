package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class LocationActivity$$ExternalSyntheticLambda18 implements RequestDelegate {
    public final /* synthetic */ LocationActivity f$0;
    public final /* synthetic */ long f$1;

    public /* synthetic */ LocationActivity$$ExternalSyntheticLambda18(LocationActivity locationActivity, long j) {
        this.f$0 = locationActivity;
        this.f$1 = j;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3168lambda$getRecentLocations$36$orgtelegramuiLocationActivity(this.f$1, tLObject, tL_error);
    }
}
