package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_account_getAuthorizationForm;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda88 implements RequestDelegate {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ int[] f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ AlertDialog f$3;
    public final /* synthetic */ TLRPC$TL_account_getAuthorizationForm f$4;
    public final /* synthetic */ String f$5;
    public final /* synthetic */ String f$6;
    public final /* synthetic */ String f$7;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda88(LaunchActivity launchActivity, int[] iArr, int i, AlertDialog alertDialog, TLRPC$TL_account_getAuthorizationForm tLRPC$TL_account_getAuthorizationForm, String str, String str2, String str3) {
        this.f$0 = launchActivity;
        this.f$1 = iArr;
        this.f$2 = i;
        this.f$3 = alertDialog;
        this.f$4 = tLRPC$TL_account_getAuthorizationForm;
        this.f$5 = str;
        this.f$6 = str2;
        this.f$7 = str3;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$runLinkRequest$56(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, tLObject, tLRPC$TL_error);
    }
}
