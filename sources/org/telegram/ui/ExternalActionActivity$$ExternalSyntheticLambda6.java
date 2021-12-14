package org.telegram.ui;

import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class ExternalActionActivity$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ ExternalActionActivity f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ TLRPC$TL_error f$2;

    public /* synthetic */ ExternalActionActivity$$ExternalSyntheticLambda6(ExternalActionActivity externalActionActivity, AlertDialog alertDialog, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0 = externalActionActivity;
        this.f$1 = alertDialog;
        this.f$2 = tLRPC$TL_error;
    }

    public final void run() {
        this.f$0.lambda$handleIntent$9(this.f$1, this.f$2);
    }
}
