package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PrivacyControlActivity$xzfj0dLNMY5psGjgrUGejhzIeyc implements Runnable {
    private final /* synthetic */ PrivacyControlActivity f$0;
    private final /* synthetic */ AlertDialog f$1;
    private final /* synthetic */ TL_error f$2;
    private final /* synthetic */ TLObject f$3;

    public /* synthetic */ -$$Lambda$PrivacyControlActivity$xzfj0dLNMY5psGjgrUGejhzIeyc(PrivacyControlActivity privacyControlActivity, AlertDialog alertDialog, TL_error tL_error, TLObject tLObject) {
        this.f$0 = privacyControlActivity;
        this.f$1 = alertDialog;
        this.f$2 = tL_error;
        this.f$3 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$null$5$PrivacyControlActivity(this.f$1, this.f$2, this.f$3);
    }
}
