package org.telegram.ui;

import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class SessionsActivity$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ SessionsActivity f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;

    public /* synthetic */ SessionsActivity$$ExternalSyntheticLambda3(SessionsActivity sessionsActivity, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0 = sessionsActivity;
        this.f$1 = tLRPC$TL_error;
    }

    public final void run() {
        this.f$0.lambda$createView$0(this.f$1);
    }
}