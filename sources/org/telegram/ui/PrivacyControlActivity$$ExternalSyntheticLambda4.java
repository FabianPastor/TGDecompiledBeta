package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class PrivacyControlActivity$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ PrivacyControlActivity f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ TLRPC$TL_error f$2;
    public final /* synthetic */ TLObject f$3;

    public /* synthetic */ PrivacyControlActivity$$ExternalSyntheticLambda4(PrivacyControlActivity privacyControlActivity, AlertDialog alertDialog, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        this.f$0 = privacyControlActivity;
        this.f$1 = alertDialog;
        this.f$2 = tLRPC$TL_error;
        this.f$3 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$applyCurrentPrivacySettings$5(this.f$1, this.f$2, this.f$3);
    }
}
