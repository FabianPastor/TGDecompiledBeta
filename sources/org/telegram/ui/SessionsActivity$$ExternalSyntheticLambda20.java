package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class SessionsActivity$$ExternalSyntheticLambda20 implements Runnable {
    public final /* synthetic */ SessionsActivity f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ TLRPC.TL_error f$3;

    public /* synthetic */ SessionsActivity$$ExternalSyntheticLambda20(SessionsActivity sessionsActivity, AlertDialog alertDialog, TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0 = sessionsActivity;
        this.f$1 = alertDialog;
        this.f$2 = tLObject;
        this.f$3 = tL_error;
    }

    public final void run() {
        this.f$0.m3866lambda$proccessQrCode$19$orgtelegramuiSessionsActivity(this.f$1, this.f$2, this.f$3);
    }
}
