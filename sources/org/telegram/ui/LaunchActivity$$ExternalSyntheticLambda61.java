package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda61 implements RequestDelegate {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ TLRPC.TL_account_authorizationForm f$3;
    public final /* synthetic */ TLRPC.TL_account_getAuthorizationForm f$4;
    public final /* synthetic */ String f$5;
    public final /* synthetic */ String f$6;
    public final /* synthetic */ String f$7;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda61(LaunchActivity launchActivity, AlertDialog alertDialog, int i, TLRPC.TL_account_authorizationForm tL_account_authorizationForm, TLRPC.TL_account_getAuthorizationForm tL_account_getAuthorizationForm, String str, String str2, String str3) {
        this.f$0 = launchActivity;
        this.f$1 = alertDialog;
        this.f$2 = i;
        this.f$3 = tL_account_authorizationForm;
        this.f$4 = tL_account_getAuthorizationForm;
        this.f$5 = str;
        this.f$6 = str2;
        this.f$7 = str3;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3102lambda$runLinkRequest$36$orgtelegramuiLaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, tLObject, tL_error);
    }
}
