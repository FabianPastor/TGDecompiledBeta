package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_authorizationForm;
import org.telegram.tgnet.TLRPC.TL_account_getAuthorizationForm;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

final /* synthetic */ class LaunchActivity$$Lambda$39 implements RequestDelegate {
    private final LaunchActivity arg$1;
    private final AlertDialog arg$2;
    private final int arg$3;
    private final TL_account_authorizationForm arg$4;
    private final TL_account_getAuthorizationForm arg$5;
    private final String arg$6;
    private final String arg$7;

    LaunchActivity$$Lambda$39(LaunchActivity launchActivity, AlertDialog alertDialog, int i, TL_account_authorizationForm tL_account_authorizationForm, TL_account_getAuthorizationForm tL_account_getAuthorizationForm, String str, String str2) {
        this.arg$1 = launchActivity;
        this.arg$2 = alertDialog;
        this.arg$3 = i;
        this.arg$4 = tL_account_authorizationForm;
        this.arg$5 = tL_account_getAuthorizationForm;
        this.arg$6 = str;
        this.arg$7 = str2;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$20$LaunchActivity(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, tLObject, tL_error);
    }
}
