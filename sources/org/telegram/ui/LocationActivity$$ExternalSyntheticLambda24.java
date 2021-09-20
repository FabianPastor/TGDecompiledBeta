package org.telegram.ui;

import org.telegram.tgnet.TLRPC$TL_messageMediaVenue;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class LocationActivity$$ExternalSyntheticLambda24 implements Runnable {
    public final /* synthetic */ LocationActivity f$0;
    public final /* synthetic */ AlertDialog[] f$1;
    public final /* synthetic */ TLRPC$TL_messageMediaVenue f$2;

    public /* synthetic */ LocationActivity$$ExternalSyntheticLambda24(LocationActivity locationActivity, AlertDialog[] alertDialogArr, TLRPC$TL_messageMediaVenue tLRPC$TL_messageMediaVenue) {
        this.f$0 = locationActivity;
        this.f$1 = alertDialogArr;
        this.f$2 = tLRPC$TL_messageMediaVenue;
    }

    public final void run() {
        this.f$0.lambda$createView$9(this.f$1, this.f$2);
    }
}
