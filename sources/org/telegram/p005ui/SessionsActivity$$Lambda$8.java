package org.telegram.p005ui;

import org.telegram.p005ui.ActionBar.AlertDialog;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_authorization;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.SessionsActivity$$Lambda$8 */
final /* synthetic */ class SessionsActivity$$Lambda$8 implements RequestDelegate {
    private final SessionsActivity arg$1;
    private final AlertDialog arg$2;
    private final TL_authorization arg$3;

    SessionsActivity$$Lambda$8(SessionsActivity sessionsActivity, AlertDialog alertDialog, TL_authorization tL_authorization) {
        this.arg$1 = sessionsActivity;
        this.arg$2 = alertDialog;
        this.arg$3 = tL_authorization;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$7$SessionsActivity(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
