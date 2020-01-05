package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocationActivity$ft2XuYls0FtkSPMyuazTNc8ZQ6A implements Runnable {
    private final /* synthetic */ LocationActivity f$0;
    private final /* synthetic */ AlertDialog[] f$1;
    private final /* synthetic */ TL_messageMediaVenue f$2;

    public /* synthetic */ -$$Lambda$LocationActivity$ft2XuYls0FtkSPMyuazTNc8ZQ6A(LocationActivity locationActivity, AlertDialog[] alertDialogArr, TL_messageMediaVenue tL_messageMediaVenue) {
        this.f$0 = locationActivity;
        this.f$1 = alertDialogArr;
        this.f$2 = tL_messageMediaVenue;
    }

    public final void run() {
        this.f$0.lambda$null$4$LocationActivity(this.f$1, this.f$2);
    }
}
