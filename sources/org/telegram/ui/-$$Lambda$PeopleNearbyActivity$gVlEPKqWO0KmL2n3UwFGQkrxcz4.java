package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PeopleNearbyActivity$gVlEPKqWO0KmL2n3UwFGQkrxcz4 implements RequestDelegate {
    private final /* synthetic */ PeopleNearbyActivity f$0;

    public /* synthetic */ -$$Lambda$PeopleNearbyActivity$gVlEPKqWO0KmL2n3UwFGQkrxcz4(PeopleNearbyActivity peopleNearbyActivity) {
        this.f$0 = peopleNearbyActivity;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$sendRequest$6$PeopleNearbyActivity(tLObject, tL_error);
    }
}