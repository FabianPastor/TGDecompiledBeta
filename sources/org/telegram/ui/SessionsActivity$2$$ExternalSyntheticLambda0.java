package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.SessionsActivity;

public final /* synthetic */ class SessionsActivity$2$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ SessionsActivity.AnonymousClass2 f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLRPC.TL_authorization f$2;

    public /* synthetic */ SessionsActivity$2$$ExternalSyntheticLambda0(SessionsActivity.AnonymousClass2 r1, TLRPC.TL_error tL_error, TLRPC.TL_authorization tL_authorization) {
        this.f$0 = r1;
        this.f$1 = tL_error;
        this.f$2 = tL_authorization;
    }

    public final void run() {
        this.f$0.m3241lambda$hide$0$orgtelegramuiSessionsActivity$2(this.f$1, this.f$2);
    }
}
