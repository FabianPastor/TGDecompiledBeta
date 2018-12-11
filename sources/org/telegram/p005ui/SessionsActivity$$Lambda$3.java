package org.telegram.p005ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.SessionsActivity$$Lambda$3 */
final /* synthetic */ class SessionsActivity$$Lambda$3 implements Runnable {
    private final SessionsActivity arg$1;
    private final TL_error arg$2;
    private final TLObject arg$3;

    SessionsActivity$$Lambda$3(SessionsActivity sessionsActivity, TL_error tL_error, TLObject tLObject) {
        this.arg$1 = sessionsActivity;
        this.arg$2 = tL_error;
        this.arg$3 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$14$SessionsActivity(this.arg$2, this.arg$3);
    }
}
