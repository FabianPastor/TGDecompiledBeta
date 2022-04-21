package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class LocationActivity$$ExternalSyntheticLambda19 implements RequestDelegate {
    public final /* synthetic */ LocationActivity f$0;
    public final /* synthetic */ AlertDialog[] f$1;
    public final /* synthetic */ TLRPC.TL_messageMediaVenue f$2;

    public /* synthetic */ LocationActivity$$ExternalSyntheticLambda19(LocationActivity locationActivity, AlertDialog[] alertDialogArr, TLRPC.TL_messageMediaVenue tL_messageMediaVenue) {
        this.f$0 = locationActivity;
        this.f$1 = alertDialogArr;
        this.f$2 = tL_messageMediaVenue;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2407lambda$createView$10$orgtelegramuiLocationActivity(this.f$1, this.f$2, tLObject, tL_error);
    }
}
