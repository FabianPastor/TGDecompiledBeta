package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ProfileActivity$UPmZCQ76huGf0mwqhk7A6RKfTkw implements RequestDelegate {
    private final /* synthetic */ ProfileActivity f$0;

    public /* synthetic */ -$$Lambda$ProfileActivity$UPmZCQ76huGf0mwqhk7A6RKfTkw(ProfileActivity profileActivity) {
        this.f$0 = profileActivity;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$createView$8$ProfileActivity(tLObject, tL_error);
    }
}
