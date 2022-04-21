package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MemberRequestsController$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ MemberRequestsController f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ long f$3;
    public final /* synthetic */ RequestDelegate f$4;

    public /* synthetic */ MemberRequestsController$$ExternalSyntheticLambda0(MemberRequestsController memberRequestsController, TLRPC.TL_error tL_error, TLObject tLObject, long j, RequestDelegate requestDelegate) {
        this.f$0 = memberRequestsController;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
        this.f$3 = j;
        this.f$4 = requestDelegate;
    }

    public final void run() {
        this.f$0.m844x92var_fc7(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
