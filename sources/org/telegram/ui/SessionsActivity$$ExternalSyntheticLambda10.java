package org.telegram.ui;

import org.telegram.tgnet.TLRPC$TL_authorization;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class SessionsActivity$$ExternalSyntheticLambda10 implements Runnable {
    public final /* synthetic */ SessionsActivity f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ TLRPC$TL_error f$2;
    public final /* synthetic */ TLRPC$TL_authorization f$3;

    public /* synthetic */ SessionsActivity$$ExternalSyntheticLambda10(SessionsActivity sessionsActivity, AlertDialog alertDialog, TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_authorization tLRPC$TL_authorization) {
        this.f$0 = sessionsActivity;
        this.f$1 = alertDialog;
        this.f$2 = tLRPC$TL_error;
        this.f$3 = tLRPC$TL_authorization;
    }

    public final void run() {
        this.f$0.lambda$createView$8(this.f$1, this.f$2, this.f$3);
    }
}
