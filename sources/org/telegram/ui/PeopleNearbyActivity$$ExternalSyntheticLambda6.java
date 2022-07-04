package org.telegram.ui;

import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class PeopleNearbyActivity$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ PeopleNearbyActivity f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;

    public /* synthetic */ PeopleNearbyActivity$$ExternalSyntheticLambda6(PeopleNearbyActivity peopleNearbyActivity, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0 = peopleNearbyActivity;
        this.f$1 = tLRPC$TL_error;
    }

    public final void run() {
        this.f$0.lambda$checkCanCreateGroup$3(this.f$1);
    }
}
