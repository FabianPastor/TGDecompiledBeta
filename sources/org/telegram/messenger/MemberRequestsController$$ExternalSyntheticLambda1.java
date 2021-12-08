package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MemberRequestsController$$ExternalSyntheticLambda1 implements RequestDelegate {
    public final /* synthetic */ MemberRequestsController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ RequestDelegate f$2;

    public /* synthetic */ MemberRequestsController$$ExternalSyntheticLambda1(MemberRequestsController memberRequestsController, long j, RequestDelegate requestDelegate) {
        this.f$0 = memberRequestsController;
        this.f$1 = j;
        this.f$2 = requestDelegate;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m909x928219c8(this.f$1, this.f$2, tLObject, tL_error);
    }
}
