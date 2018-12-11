package org.telegram.p005ui.Components;

import org.telegram.p005ui.ActionBar.AlertDialog;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.Components.TermsOfServiceView$$Lambda$7 */
final /* synthetic */ class TermsOfServiceView$$Lambda$7 implements Runnable {
    private final TermsOfServiceView arg$1;
    private final AlertDialog arg$2;
    private final TLObject arg$3;
    private final TL_error arg$4;

    TermsOfServiceView$$Lambda$7(TermsOfServiceView termsOfServiceView, AlertDialog alertDialog, TLObject tLObject, TL_error tL_error) {
        this.arg$1 = termsOfServiceView;
        this.arg$2 = alertDialog;
        this.arg$3 = tLObject;
        this.arg$4 = tL_error;
    }

    public void run() {
        this.arg$1.lambda$null$0$TermsOfServiceView(this.arg$2, this.arg$3, this.arg$4);
    }
}
