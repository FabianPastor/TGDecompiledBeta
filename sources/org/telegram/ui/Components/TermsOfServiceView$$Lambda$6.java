package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

final /* synthetic */ class TermsOfServiceView$$Lambda$6 implements RequestDelegate {
    private final TermsOfServiceView arg$1;
    private final AlertDialog arg$2;

    TermsOfServiceView$$Lambda$6(TermsOfServiceView termsOfServiceView, AlertDialog alertDialog) {
        this.arg$1 = termsOfServiceView;
        this.arg$2 = alertDialog;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$1$TermsOfServiceView(this.arg$2, tLObject, tL_error);
    }
}
