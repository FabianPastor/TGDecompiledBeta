package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_webAuthorization;
import org.telegram.ui.ActionBar.AlertDialog;

final /* synthetic */ class SessionsActivity$$Lambda$10 implements Runnable {
    private final SessionsActivity arg$1;
    private final AlertDialog arg$2;
    private final TL_error arg$3;
    private final TL_webAuthorization arg$4;

    SessionsActivity$$Lambda$10(SessionsActivity sessionsActivity, AlertDialog alertDialog, TL_error tL_error, TL_webAuthorization tL_webAuthorization) {
        this.arg$1 = sessionsActivity;
        this.arg$2 = alertDialog;
        this.arg$3 = tL_error;
        this.arg$4 = tL_webAuthorization;
    }

    public void run() {
        this.arg$1.lambda$null$8$SessionsActivity(this.arg$2, this.arg$3, this.arg$4);
    }
}
