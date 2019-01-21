package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

final /* synthetic */ class PrivacyControlActivity$$Lambda$1 implements RequestDelegate {
    private final PrivacyControlActivity arg$1;
    private final AlertDialog arg$2;

    PrivacyControlActivity$$Lambda$1(PrivacyControlActivity privacyControlActivity, AlertDialog alertDialog) {
        this.arg$1 = privacyControlActivity;
        this.arg$2 = alertDialog;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$applyCurrentPrivacySettings$4$PrivacyControlActivity(this.arg$2, tLObject, tL_error);
    }
}
