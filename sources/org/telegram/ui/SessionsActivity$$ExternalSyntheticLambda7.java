package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class SessionsActivity$$ExternalSyntheticLambda7 implements RequestDelegate {
    public final /* synthetic */ SessionsActivity f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ TLRPC.TL_webAuthorization f$2;

    public /* synthetic */ SessionsActivity$$ExternalSyntheticLambda7(SessionsActivity sessionsActivity, AlertDialog alertDialog, TLRPC.TL_webAuthorization tL_webAuthorization) {
        this.f$0 = sessionsActivity;
        this.f$1 = alertDialog;
        this.f$2 = tL_webAuthorization;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3226lambda$createView$11$orgtelegramuiSessionsActivity(this.f$1, this.f$2, tLObject, tL_error);
    }
}
