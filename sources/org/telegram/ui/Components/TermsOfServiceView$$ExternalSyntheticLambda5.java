package org.telegram.ui.Components;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class TermsOfServiceView$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ TermsOfServiceView f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ TLRPC$TL_error f$3;

    public /* synthetic */ TermsOfServiceView$$ExternalSyntheticLambda5(TermsOfServiceView termsOfServiceView, AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0 = termsOfServiceView;
        this.f$1 = alertDialog;
        this.f$2 = tLObject;
        this.f$3 = tLRPC$TL_error;
    }

    public final void run() {
        this.f$0.lambda$new$0(this.f$1, this.f$2, this.f$3);
    }
}