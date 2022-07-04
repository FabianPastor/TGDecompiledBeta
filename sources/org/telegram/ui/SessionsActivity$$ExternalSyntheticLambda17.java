package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SessionsActivity$$ExternalSyntheticLambda17 implements Runnable {
    public final /* synthetic */ SessionsActivity f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLObject f$2;

    public /* synthetic */ SessionsActivity$$ExternalSyntheticLambda17(SessionsActivity sessionsActivity, TLRPC.TL_error tL_error, TLObject tLObject) {
        this.f$0 = sessionsActivity;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.m4592lambda$createView$4$orgtelegramuiSessionsActivity(this.f$1, this.f$2);
    }
}
