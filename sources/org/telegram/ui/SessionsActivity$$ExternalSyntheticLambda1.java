package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class SessionsActivity$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ SessionsActivity f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ TLRPC.TL_error f$2;
    public final /* synthetic */ TLRPC.TL_webAuthorization f$3;

    public /* synthetic */ SessionsActivity$$ExternalSyntheticLambda1(SessionsActivity sessionsActivity, AlertDialog alertDialog, TLRPC.TL_error tL_error, TLRPC.TL_webAuthorization tL_webAuthorization) {
        this.f$0 = sessionsActivity;
        this.f$1 = alertDialog;
        this.f$2 = tL_error;
        this.f$3 = tL_webAuthorization;
    }

    public final void run() {
        this.f$0.m3225lambda$createView$10$orgtelegramuiSessionsActivity(this.f$1, this.f$2, this.f$3);
    }
}
