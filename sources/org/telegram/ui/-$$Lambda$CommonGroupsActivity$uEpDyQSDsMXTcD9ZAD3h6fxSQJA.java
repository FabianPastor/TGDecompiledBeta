package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$CommonGroupsActivity$uEpDyQSDsMXTcD9ZAD3h6fxSQJA implements RequestDelegate {
    private final /* synthetic */ CommonGroupsActivity f$0;
    private final /* synthetic */ int f$1;

    public /* synthetic */ -$$Lambda$CommonGroupsActivity$uEpDyQSDsMXTcD9ZAD3h6fxSQJA(CommonGroupsActivity commonGroupsActivity, int i) {
        this.f$0 = commonGroupsActivity;
        this.f$1 = i;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$getChats$2$CommonGroupsActivity(this.f$1, tLObject, tL_error);
    }
}
