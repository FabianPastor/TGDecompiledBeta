package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_authorizationForm;
import org.telegram.tgnet.TLRPC.TL_account_getAuthorizationForm;
import org.telegram.ui.ActionBar.AlertDialog;

final /* synthetic */ class LaunchActivity$$Lambda$41 implements Runnable {
    private final LaunchActivity arg$1;
    private final AlertDialog arg$2;
    private final TLObject arg$3;
    private final int arg$4;
    private final TL_account_authorizationForm arg$5;
    private final TL_account_getAuthorizationForm arg$6;
    private final String arg$7;
    private final String arg$8;

    LaunchActivity$$Lambda$41(LaunchActivity launchActivity, AlertDialog alertDialog, TLObject tLObject, int i, TL_account_authorizationForm tL_account_authorizationForm, TL_account_getAuthorizationForm tL_account_getAuthorizationForm, String str, String str2) {
        this.arg$1 = launchActivity;
        this.arg$2 = alertDialog;
        this.arg$3 = tLObject;
        this.arg$4 = i;
        this.arg$5 = tL_account_authorizationForm;
        this.arg$6 = tL_account_getAuthorizationForm;
        this.arg$7 = str;
        this.arg$8 = str2;
    }

    public void run() {
        this.arg$1.lambda$null$19$LaunchActivity(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8);
    }
}
