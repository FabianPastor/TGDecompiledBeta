package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.SessionsActivity;

public final /* synthetic */ class SessionsActivity$5$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ SessionsActivity.AnonymousClass5 f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ TLRPC.TL_error f$2;
    public final /* synthetic */ Runnable f$3;

    public /* synthetic */ SessionsActivity$5$$ExternalSyntheticLambda3(SessionsActivity.AnonymousClass5 r1, TLObject tLObject, TLRPC.TL_error tL_error, Runnable runnable) {
        this.f$0 = r1;
        this.f$1 = tLObject;
        this.f$2 = tL_error;
        this.f$3 = runnable;
    }

    public final void run() {
        this.f$0.m3244lambda$processQr$1$orgtelegramuiSessionsActivity$5(this.f$1, this.f$2, this.f$3);
    }
}
