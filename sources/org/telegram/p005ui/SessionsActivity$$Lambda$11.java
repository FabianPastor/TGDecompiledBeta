package org.telegram.p005ui;

import org.telegram.p005ui.ActionBar.AlertDialog;
import org.telegram.tgnet.TLRPC.TL_authorization;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.SessionsActivity$$Lambda$11 */
final /* synthetic */ class SessionsActivity$$Lambda$11 implements Runnable {
    private final SessionsActivity arg$1;
    private final AlertDialog arg$2;
    private final TL_error arg$3;
    private final TL_authorization arg$4;

    SessionsActivity$$Lambda$11(SessionsActivity sessionsActivity, AlertDialog alertDialog, TL_error tL_error, TL_authorization tL_authorization) {
        this.arg$1 = sessionsActivity;
        this.arg$2 = alertDialog;
        this.arg$3 = tL_error;
        this.arg$4 = tL_authorization;
    }

    public void run() {
        this.arg$1.lambda$null$6$SessionsActivity(this.arg$2, this.arg$3, this.arg$4);
    }
}
