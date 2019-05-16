package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_authorization;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SessionsActivity$uCzzVW_FF8MV8J-oXGgpzlkkOI4 implements RequestDelegate {
    private final /* synthetic */ SessionsActivity f$0;
    private final /* synthetic */ AlertDialog f$1;
    private final /* synthetic */ TL_authorization f$2;

    public /* synthetic */ -$$Lambda$SessionsActivity$uCzzVW_FF8MV8J-oXGgpzlkkOI4(SessionsActivity sessionsActivity, AlertDialog alertDialog, TL_authorization tL_authorization) {
        this.f$0 = sessionsActivity;
        this.f$1 = alertDialog;
        this.f$2 = tL_authorization;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$7$SessionsActivity(this.f$1, this.f$2, tLObject, tL_error);
    }
}
