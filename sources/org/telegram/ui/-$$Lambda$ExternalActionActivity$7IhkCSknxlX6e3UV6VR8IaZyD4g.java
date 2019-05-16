package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_getAuthorizationForm;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ExternalActionActivity$7IhkCSknxlX6e3UV6VR8IaZyD4g implements RequestDelegate {
    private final /* synthetic */ ExternalActionActivity f$0;
    private final /* synthetic */ int[] f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ AlertDialog f$3;
    private final /* synthetic */ TL_account_getAuthorizationForm f$4;
    private final /* synthetic */ String f$5;
    private final /* synthetic */ String f$6;

    public /* synthetic */ -$$Lambda$ExternalActionActivity$7IhkCSknxlX6e3UV6VR8IaZyD4g(ExternalActionActivity externalActionActivity, int[] iArr, int i, AlertDialog alertDialog, TL_account_getAuthorizationForm tL_account_getAuthorizationForm, String str, String str2) {
        this.f$0 = externalActionActivity;
        this.f$1 = iArr;
        this.f$2 = i;
        this.f$3 = alertDialog;
        this.f$4 = tL_account_getAuthorizationForm;
        this.f$5 = str;
        this.f$6 = str2;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$handleIntent$10$ExternalActionActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, tLObject, tL_error);
    }
}
