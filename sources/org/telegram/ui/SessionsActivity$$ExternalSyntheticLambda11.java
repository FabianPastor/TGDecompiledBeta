package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class SessionsActivity$$ExternalSyntheticLambda11 implements RequestDelegate {
    public final /* synthetic */ SessionsActivity f$0;

    public /* synthetic */ SessionsActivity$$ExternalSyntheticLambda11(SessionsActivity sessionsActivity) {
        this.f$0 = sessionsActivity;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadSessions$19(tLObject, tLRPC$TL_error);
    }
}