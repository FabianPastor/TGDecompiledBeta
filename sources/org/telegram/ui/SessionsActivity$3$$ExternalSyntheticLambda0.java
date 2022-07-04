package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.SessionsActivity;

public final /* synthetic */ class SessionsActivity$3$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ SessionsActivity.AnonymousClass3 f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLRPC.TL_authorization f$2;

    public /* synthetic */ SessionsActivity$3$$ExternalSyntheticLambda0(SessionsActivity.AnonymousClass3 r1, TLRPC.TL_error tL_error, TLRPC.TL_authorization tL_authorization) {
        this.f$0 = r1;
        this.f$1 = tL_error;
        this.f$2 = tL_authorization;
    }

    public final void run() {
        this.f$0.m4604lambda$hide$0$orgtelegramuiSessionsActivity$3(this.f$1, this.f$2);
    }
}
