package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class ExternalActionActivity$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ ExternalActionActivity f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ TLRPC.TL_account_authorizationForm f$4;
    public final /* synthetic */ TLRPC.TL_account_getAuthorizationForm f$5;
    public final /* synthetic */ String f$6;
    public final /* synthetic */ String f$7;

    public /* synthetic */ ExternalActionActivity$$ExternalSyntheticLambda6(ExternalActionActivity externalActionActivity, AlertDialog alertDialog, TLObject tLObject, int i, TLRPC.TL_account_authorizationForm tL_account_authorizationForm, TLRPC.TL_account_getAuthorizationForm tL_account_getAuthorizationForm, String str, String str2) {
        this.f$0 = externalActionActivity;
        this.f$1 = alertDialog;
        this.f$2 = tLObject;
        this.f$3 = i;
        this.f$4 = tL_account_authorizationForm;
        this.f$5 = tL_account_getAuthorizationForm;
        this.f$6 = str;
        this.f$7 = str2;
    }

    public final void run() {
        this.f$0.m2137lambda$handleIntent$6$orgtelegramuiExternalActionActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}
