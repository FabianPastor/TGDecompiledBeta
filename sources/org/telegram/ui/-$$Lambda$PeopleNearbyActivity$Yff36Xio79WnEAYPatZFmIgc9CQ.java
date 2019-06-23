package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PeopleNearbyActivity$Yfvar_Xio79WnEAYPatZFmIgc9CQ implements RequestDelegate {
    private final /* synthetic */ PeopleNearbyActivity f$0;

    public /* synthetic */ -$$Lambda$PeopleNearbyActivity$Yfvar_Xio79WnEAYPatZFmIgc9CQ(PeopleNearbyActivity peopleNearbyActivity) {
        this.f$0 = peopleNearbyActivity;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$checkCanCreateGroup$3$PeopleNearbyActivity(tLObject, tL_error);
    }
}
