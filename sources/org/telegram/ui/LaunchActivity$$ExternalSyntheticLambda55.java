package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_account_authorizationForm;
import org.telegram.tgnet.TLRPC$TL_account_getAuthorizationForm;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda55 implements Runnable {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ TLRPC$TL_account_authorizationForm f$4;
    public final /* synthetic */ TLRPC$TL_account_getAuthorizationForm f$5;
    public final /* synthetic */ String f$6;
    public final /* synthetic */ String f$7;
    public final /* synthetic */ String f$8;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda55(LaunchActivity launchActivity, AlertDialog alertDialog, TLObject tLObject, int i, TLRPC$TL_account_authorizationForm tLRPC$TL_account_authorizationForm, TLRPC$TL_account_getAuthorizationForm tLRPC$TL_account_getAuthorizationForm, String str, String str2, String str3) {
        this.f$0 = launchActivity;
        this.f$1 = alertDialog;
        this.f$2 = tLObject;
        this.f$3 = i;
        this.f$4 = tLRPC$TL_account_authorizationForm;
        this.f$5 = tLRPC$TL_account_getAuthorizationForm;
        this.f$6 = str;
        this.f$7 = str2;
        this.f$8 = str3;
    }

    public final void run() {
        this.f$0.lambda$runLinkRequest$54(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
    }
}
