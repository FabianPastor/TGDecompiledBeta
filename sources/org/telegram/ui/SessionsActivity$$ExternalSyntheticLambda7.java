package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class SessionsActivity$$ExternalSyntheticLambda7 implements RequestDelegate {
    public final /* synthetic */ SessionsActivity f$0;
    public final /* synthetic */ AlertDialog f$1;

    public /* synthetic */ SessionsActivity$$ExternalSyntheticLambda7(SessionsActivity sessionsActivity, AlertDialog alertDialog) {
        this.f$0 = sessionsActivity;
        this.f$1 = alertDialog;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3867lambda$proccessQrCode$20$orgtelegramuiSessionsActivity(this.f$1, tLObject, tL_error);
    }
}
