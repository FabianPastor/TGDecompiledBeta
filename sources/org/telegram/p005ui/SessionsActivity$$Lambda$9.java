package org.telegram.p005ui;

import org.telegram.p005ui.ActionBar.AlertDialog;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_webAuthorization;

/* renamed from: org.telegram.ui.SessionsActivity$$Lambda$9 */
final /* synthetic */ class SessionsActivity$$Lambda$9 implements RequestDelegate {
    private final SessionsActivity arg$1;
    private final AlertDialog arg$2;
    private final TL_webAuthorization arg$3;

    SessionsActivity$$Lambda$9(SessionsActivity sessionsActivity, AlertDialog alertDialog, TL_webAuthorization tL_webAuthorization) {
        this.arg$1 = sessionsActivity;
        this.arg$2 = alertDialog;
        this.arg$3 = tL_webAuthorization;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$9$SessionsActivity(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
