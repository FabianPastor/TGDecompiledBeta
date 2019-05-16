package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_authorizationForm;
import org.telegram.tgnet.TLRPC.TL_account_getAuthorizationForm;
import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ExternalActionActivity$Okpye-XNIzLiHpMbfSsCYU0NOVg implements Runnable {
    private final /* synthetic */ ExternalActionActivity f$0;
    private final /* synthetic */ AlertDialog f$1;
    private final /* synthetic */ TLObject f$2;
    private final /* synthetic */ int f$3;
    private final /* synthetic */ TL_account_authorizationForm f$4;
    private final /* synthetic */ TL_account_getAuthorizationForm f$5;
    private final /* synthetic */ String f$6;
    private final /* synthetic */ String f$7;

    public /* synthetic */ -$$Lambda$ExternalActionActivity$Okpye-XNIzLiHpMbfSsCYU0NOVg(ExternalActionActivity externalActionActivity, AlertDialog alertDialog, TLObject tLObject, int i, TL_account_authorizationForm tL_account_authorizationForm, TL_account_getAuthorizationForm tL_account_getAuthorizationForm, String str, String str2) {
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
        this.f$0.lambda$null$6$ExternalActionActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}
