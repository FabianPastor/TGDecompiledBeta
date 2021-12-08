package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class GroupCallActivity$$ExternalSyntheticLambda47 implements RequestDelegate {
    public final /* synthetic */ GroupCallActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda47(GroupCallActivity groupCallActivity, int i, boolean z) {
        this.f$0 = groupCallActivity;
        this.f$1 = i;
        this.f$2 = z;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$getLink$41(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
