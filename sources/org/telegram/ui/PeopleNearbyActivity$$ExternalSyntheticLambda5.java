package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class PeopleNearbyActivity$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ PeopleNearbyActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ TLRPC.TL_error f$2;
    public final /* synthetic */ TLObject f$3;

    public /* synthetic */ PeopleNearbyActivity$$ExternalSyntheticLambda5(PeopleNearbyActivity peopleNearbyActivity, int i, TLRPC.TL_error tL_error, TLObject tLObject) {
        this.f$0 = peopleNearbyActivity;
        this.f$1 = i;
        this.f$2 = tL_error;
        this.f$3 = tLObject;
    }

    public final void run() {
        this.f$0.m2874lambda$sendRequest$6$orgtelegramuiPeopleNearbyActivity(this.f$1, this.f$2, this.f$3);
    }
}
