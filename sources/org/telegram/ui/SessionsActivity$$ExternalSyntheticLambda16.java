package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_webAuthorization;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class SessionsActivity$$ExternalSyntheticLambda16 implements RequestDelegate {
    public final /* synthetic */ SessionsActivity f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ TLRPC$TL_webAuthorization f$2;

    public /* synthetic */ SessionsActivity$$ExternalSyntheticLambda16(SessionsActivity sessionsActivity, AlertDialog alertDialog, TLRPC$TL_webAuthorization tLRPC$TL_webAuthorization) {
        this.f$0 = sessionsActivity;
        this.f$1 = alertDialog;
        this.f$2 = tLRPC$TL_webAuthorization;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$createView$11(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
