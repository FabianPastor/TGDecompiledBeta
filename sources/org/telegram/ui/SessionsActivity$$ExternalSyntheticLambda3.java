package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SessionsActivity$$ExternalSyntheticLambda3 implements RequestDelegate {
    public final /* synthetic */ SessionsActivity f$0;

    public /* synthetic */ SessionsActivity$$ExternalSyntheticLambda3(SessionsActivity sessionsActivity) {
        this.f$0 = sessionsActivity;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3855lambda$createView$3$orgtelegramuiSessionsActivity(tLObject, tL_error);
    }
}
