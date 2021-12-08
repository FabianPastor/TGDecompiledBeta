package org.telegram.ui;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SessionsActivity$$ExternalSyntheticLambda15 implements Runnable {
    public final /* synthetic */ SessionsActivity f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;

    public /* synthetic */ SessionsActivity$$ExternalSyntheticLambda15(SessionsActivity sessionsActivity, TLRPC.TL_error tL_error) {
        this.f$0 = sessionsActivity;
        this.f$1 = tL_error;
    }

    public final void run() {
        this.f$0.m3865lambda$proccessQrCode$18$orgtelegramuiSessionsActivity(this.f$1);
    }
}
