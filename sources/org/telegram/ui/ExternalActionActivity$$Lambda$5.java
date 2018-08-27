package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_getAuthorizationForm;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

final /* synthetic */ class ExternalActionActivity$$Lambda$5 implements RequestDelegate {
    private final ExternalActionActivity arg$1;
    private final int[] arg$2;
    private final int arg$3;
    private final AlertDialog arg$4;
    private final TL_account_getAuthorizationForm arg$5;
    private final String arg$6;

    ExternalActionActivity$$Lambda$5(ExternalActionActivity externalActionActivity, int[] iArr, int i, AlertDialog alertDialog, TL_account_getAuthorizationForm tL_account_getAuthorizationForm, String str) {
        this.arg$1 = externalActionActivity;
        this.arg$2 = iArr;
        this.arg$3 = i;
        this.arg$4 = alertDialog;
        this.arg$5 = tL_account_getAuthorizationForm;
        this.arg$6 = str;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$handleIntent$9$ExternalActionActivity(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, tLObject, tL_error);
    }
}
