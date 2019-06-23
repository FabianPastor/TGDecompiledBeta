package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocationActivity$JGVJcTpeqmcZpqQe4AImaLh0UqE implements Runnable {
    private final /* synthetic */ LocationActivity f$0;
    private final /* synthetic */ AlertDialog[] f$1;
    private final /* synthetic */ TL_messageMediaVenue f$2;

    public /* synthetic */ -$$Lambda$LocationActivity$JGVJcTpeqmcZpqQe4AImaLh0UqE(LocationActivity locationActivity, AlertDialog[] alertDialogArr, TL_messageMediaVenue tL_messageMediaVenue) {
        this.f$0 = locationActivity;
        this.f$1 = alertDialogArr;
        this.f$2 = tL_messageMediaVenue;
    }

    public final void run() {
        this.f$0.lambda$null$0$LocationActivity(this.f$1, this.f$2);
    }
}
