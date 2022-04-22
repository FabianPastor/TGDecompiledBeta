package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class PeopleNearbyActivity$$ExternalSyntheticLambda7 implements RequestDelegate {
    public final /* synthetic */ PeopleNearbyActivity f$0;

    public /* synthetic */ PeopleNearbyActivity$$ExternalSyntheticLambda7(PeopleNearbyActivity peopleNearbyActivity) {
        this.f$0 = peopleNearbyActivity;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$checkCanCreateGroup$4(tLObject, tLRPC$TL_error);
    }
}
