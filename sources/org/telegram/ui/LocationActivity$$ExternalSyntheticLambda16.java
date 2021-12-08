package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class LocationActivity$$ExternalSyntheticLambda16 implements Runnable {
    public final /* synthetic */ LocationActivity f$0;
    public final /* synthetic */ AlertDialog[] f$1;
    public final /* synthetic */ TLRPC.TL_messageMediaVenue f$2;

    public /* synthetic */ LocationActivity$$ExternalSyntheticLambda16(LocationActivity locationActivity, AlertDialog[] alertDialogArr, TLRPC.TL_messageMediaVenue tL_messageMediaVenue) {
        this.f$0 = locationActivity;
        this.f$1 = alertDialogArr;
        this.f$2 = tL_messageMediaVenue;
    }

    public final void run() {
        this.f$0.m3164lambda$createView$9$orgtelegramuiLocationActivity(this.f$1, this.f$2);
    }
}
