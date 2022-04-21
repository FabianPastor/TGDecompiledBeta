package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class PeopleNearbyActivity$$ExternalSyntheticLambda9 implements RequestDelegate {
    public final /* synthetic */ PeopleNearbyActivity f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ PeopleNearbyActivity$$ExternalSyntheticLambda9(PeopleNearbyActivity peopleNearbyActivity, int i) {
        this.f$0 = peopleNearbyActivity;
        this.f$1 = i;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2875lambda$sendRequest$7$orgtelegramuiPeopleNearbyActivity(this.f$1, tLObject, tL_error);
    }
}
