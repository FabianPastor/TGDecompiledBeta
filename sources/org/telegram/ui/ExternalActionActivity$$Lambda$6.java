package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_authorizationForm;
import org.telegram.tgnet.TLRPC.TL_account_getAuthorizationForm;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

final /* synthetic */ class ExternalActionActivity$$Lambda$6 implements RequestDelegate {
    private final ExternalActionActivity arg$1;
    private final AlertDialog arg$2;
    private final int arg$3;
    private final TL_account_authorizationForm arg$4;
    private final TL_account_getAuthorizationForm arg$5;
    private final String arg$6;

    ExternalActionActivity$$Lambda$6(ExternalActionActivity externalActionActivity, AlertDialog alertDialog, int i, TL_account_authorizationForm tL_account_authorizationForm, TL_account_getAuthorizationForm tL_account_getAuthorizationForm, String str) {
        this.arg$1 = externalActionActivity;
        this.arg$2 = alertDialog;
        this.arg$3 = i;
        this.arg$4 = tL_account_authorizationForm;
        this.arg$5 = tL_account_getAuthorizationForm;
        this.arg$6 = str;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$6$ExternalActionActivity(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, tLObject, tL_error);
    }
}
