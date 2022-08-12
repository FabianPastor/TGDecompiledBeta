package org.telegram.ui;

import org.telegram.tgnet.TLObject;

public final /* synthetic */ class LocationActivity$$ExternalSyntheticLambda26 implements Runnable {
    public final /* synthetic */ LocationActivity f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ long f$2;

    public /* synthetic */ LocationActivity$$ExternalSyntheticLambda26(LocationActivity locationActivity, TLObject tLObject, long j) {
        this.f$0 = locationActivity;
        this.f$1 = tLObject;
        this.f$2 = j;
    }

    public final void run() {
        this.f$0.lambda$getRecentLocations$39(this.f$1, this.f$2);
    }
}
