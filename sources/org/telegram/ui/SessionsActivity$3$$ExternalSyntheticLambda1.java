package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.SessionsActivity;

public final /* synthetic */ class SessionsActivity$3$$ExternalSyntheticLambda1 implements RequestDelegate {
    public final /* synthetic */ SessionsActivity.AnonymousClass3 f$0;
    public final /* synthetic */ TLRPC.TL_authorization f$1;

    public /* synthetic */ SessionsActivity$3$$ExternalSyntheticLambda1(SessionsActivity.AnonymousClass3 r1, TLRPC.TL_authorization tL_authorization) {
        this.f$0 = r1;
        this.f$1 = tL_authorization;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m4605lambda$hide$1$orgtelegramuiSessionsActivity$3(this.f$1, tLObject, tL_error);
    }
}
