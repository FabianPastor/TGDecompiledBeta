package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class SessionsActivity$$Lambda$13 implements RequestDelegate {
    private final SessionsActivity arg$1;

    SessionsActivity$$Lambda$13(SessionsActivity sessionsActivity) {
        this.arg$1 = sessionsActivity;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$3$SessionsActivity(tLObject, tL_error);
    }
}