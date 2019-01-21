package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

final /* synthetic */ class PrivacyControlActivity$$Lambda$2 implements Runnable {
    private final PrivacyControlActivity arg$1;
    private final AlertDialog arg$2;
    private final TL_error arg$3;
    private final TLObject arg$4;

    PrivacyControlActivity$$Lambda$2(PrivacyControlActivity privacyControlActivity, AlertDialog alertDialog, TL_error tL_error, TLObject tLObject) {
        this.arg$1 = privacyControlActivity;
        this.arg$2 = alertDialog;
        this.arg$3 = tL_error;
        this.arg$4 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$3$PrivacyControlActivity(this.arg$2, this.arg$3, this.arg$4);
    }
}
