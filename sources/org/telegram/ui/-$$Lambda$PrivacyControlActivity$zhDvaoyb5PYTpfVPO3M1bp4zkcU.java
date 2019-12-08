package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PrivacyControlActivity$zhDvaoyb5PYTpfVPO3M1bp4zkcU implements RequestDelegate {
    private final /* synthetic */ PrivacyControlActivity f$0;
    private final /* synthetic */ AlertDialog f$1;

    public /* synthetic */ -$$Lambda$PrivacyControlActivity$zhDvaoyb5PYTpfVPO3M1bp4zkcU(PrivacyControlActivity privacyControlActivity, AlertDialog alertDialog) {
        this.f$0 = privacyControlActivity;
        this.f$1 = alertDialog;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$applyCurrentPrivacySettings$6$PrivacyControlActivity(this.f$1, tLObject, tL_error);
    }
}
