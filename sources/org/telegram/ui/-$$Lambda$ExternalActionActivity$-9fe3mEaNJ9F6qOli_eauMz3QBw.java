package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ExternalActionActivity$-9fe3mEaNJ9F6qOli_eauMz3QBw implements Runnable {
    private final /* synthetic */ ExternalActionActivity f$0;
    private final /* synthetic */ AlertDialog f$1;
    private final /* synthetic */ TL_error f$2;

    public /* synthetic */ -$$Lambda$ExternalActionActivity$-9fe3mEaNJ9F6qOli_eauMz3QBw(ExternalActionActivity externalActionActivity, AlertDialog alertDialog, TL_error tL_error) {
        this.f$0 = externalActionActivity;
        this.f$1 = alertDialog;
        this.f$2 = tL_error;
    }

    public final void run() {
        this.f$0.lambda$null$9$ExternalActionActivity(this.f$1, this.f$2);
    }
}
