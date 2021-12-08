package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class GroupCallActivity$$ExternalSyntheticLambda41 implements RequestDelegate {
    public final /* synthetic */ GroupCallActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda41(GroupCallActivity groupCallActivity, int i, boolean z) {
        this.f$0 = groupCallActivity;
        this.f$1 = i;
        this.f$2 = z;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2951lambda$getLink$41$orgtelegramuiGroupCallActivity(this.f$1, this.f$2, tLObject, tL_error);
    }
}
