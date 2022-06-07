package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class SessionsActivity$$ExternalSyntheticLambda18 implements RequestDelegate {
    public final /* synthetic */ SessionsActivity f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ SessionsActivity$$ExternalSyntheticLambda18(SessionsActivity sessionsActivity, boolean z) {
        this.f$0 = sessionsActivity;
        this.f$1 = z;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadSessions$16(this.f$1, tLObject, tLRPC$TL_error);
    }
}
