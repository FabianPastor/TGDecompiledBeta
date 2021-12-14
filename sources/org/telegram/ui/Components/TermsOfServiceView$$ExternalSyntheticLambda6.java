package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class TermsOfServiceView$$ExternalSyntheticLambda6 implements RequestDelegate {
    public final /* synthetic */ TermsOfServiceView f$0;
    public final /* synthetic */ AlertDialog f$1;

    public /* synthetic */ TermsOfServiceView$$ExternalSyntheticLambda6(TermsOfServiceView termsOfServiceView, AlertDialog alertDialog) {
        this.f$0 = termsOfServiceView;
        this.f$1 = alertDialog;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$new$1(this.f$1, tLObject, tLRPC$TL_error);
    }
}
