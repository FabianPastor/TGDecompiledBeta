package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class PeopleNearbyActivity$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ PeopleNearbyActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ TLRPC$TL_error f$2;
    public final /* synthetic */ TLObject f$3;

    public /* synthetic */ PeopleNearbyActivity$$ExternalSyntheticLambda4(PeopleNearbyActivity peopleNearbyActivity, int i, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        this.f$0 = peopleNearbyActivity;
        this.f$1 = i;
        this.f$2 = tLRPC$TL_error;
        this.f$3 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$sendRequest$6(this.f$1, this.f$2, this.f$3);
    }
}
