package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_webAuthorization;
import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SessionsActivity$sjBDbKRuMyWfdQovBWSND8F1Gd4 implements Runnable {
    private final /* synthetic */ SessionsActivity f$0;
    private final /* synthetic */ AlertDialog f$1;
    private final /* synthetic */ TL_error f$2;
    private final /* synthetic */ TL_webAuthorization f$3;

    public /* synthetic */ -$$Lambda$SessionsActivity$sjBDbKRuMyWfdQovBWSND8F1Gd4(SessionsActivity sessionsActivity, AlertDialog alertDialog, TL_error tL_error, TL_webAuthorization tL_webAuthorization) {
        this.f$0 = sessionsActivity;
        this.f$1 = alertDialog;
        this.f$2 = tL_error;
        this.f$3 = tL_webAuthorization;
    }

    public final void run() {
        this.f$0.lambda$null$8$SessionsActivity(this.f$1, this.f$2, this.f$3);
    }
}
