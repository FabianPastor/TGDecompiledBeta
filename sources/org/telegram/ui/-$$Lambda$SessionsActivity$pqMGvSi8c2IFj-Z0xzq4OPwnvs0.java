package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_webAuthorization;
import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SessionsActivity$pqMGvSi8c2IFj-Z0xzq4OPwnvs0 implements RequestDelegate {
    private final /* synthetic */ SessionsActivity f$0;
    private final /* synthetic */ AlertDialog f$1;
    private final /* synthetic */ TL_webAuthorization f$2;

    public /* synthetic */ -$$Lambda$SessionsActivity$pqMGvSi8c2IFj-Z0xzq4OPwnvs0(SessionsActivity sessionsActivity, AlertDialog alertDialog, TL_webAuthorization tL_webAuthorization) {
        this.f$0 = sessionsActivity;
        this.f$1 = alertDialog;
        this.f$2 = tL_webAuthorization;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$13$SessionsActivity(this.f$1, this.f$2, tLObject, tL_error);
    }
}