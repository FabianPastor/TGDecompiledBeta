package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocationActivity$pKF2K0fO3wWfSkQU37TiB-NSoZU implements RequestDelegate {
    private final /* synthetic */ LocationActivity f$0;
    private final /* synthetic */ AlertDialog[] f$1;
    private final /* synthetic */ TL_messageMediaVenue f$2;

    public /* synthetic */ -$$Lambda$LocationActivity$pKF2K0fO3wWfSkQU37TiB-NSoZU(LocationActivity locationActivity, AlertDialog[] alertDialogArr, TL_messageMediaVenue tL_messageMediaVenue) {
        this.f$0 = locationActivity;
        this.f$1 = alertDialogArr;
        this.f$2 = tL_messageMediaVenue;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$5$LocationActivity(this.f$1, this.f$2, tLObject, tL_error);
    }
}
