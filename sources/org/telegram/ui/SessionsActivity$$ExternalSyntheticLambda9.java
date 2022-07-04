package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SessionsActivity$$ExternalSyntheticLambda9 implements RequestDelegate {
    public final /* synthetic */ SessionsActivity f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ SessionsActivity$$ExternalSyntheticLambda9(SessionsActivity sessionsActivity, boolean z) {
        this.f$0 = sessionsActivity;
        this.f$1 = z;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m4602lambda$loadSessions$19$orgtelegramuiSessionsActivity(this.f$1, tLObject, tL_error);
    }
}
