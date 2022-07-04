package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.SessionsActivity;

public final /* synthetic */ class SessionsActivity$5$$ExternalSyntheticLambda4 implements RequestDelegate {
    public final /* synthetic */ SessionsActivity.AnonymousClass5 f$0;
    public final /* synthetic */ Runnable f$1;

    public /* synthetic */ SessionsActivity$5$$ExternalSyntheticLambda4(SessionsActivity.AnonymousClass5 r1, Runnable runnable) {
        this.f$0 = r1;
        this.f$1 = runnable;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m4608lambda$processQr$2$orgtelegramuiSessionsActivity$5(this.f$1, tLObject, tL_error);
    }
}
