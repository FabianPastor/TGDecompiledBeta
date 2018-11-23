package org.telegram.p005ui;

import org.telegram.p005ui.ActionBar.AlertDialog;
import org.telegram.tgnet.TLRPC.TL_account_updateProfile;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.ChangeBioActivity$$Lambda$5 */
final /* synthetic */ class ChangeBioActivity$$Lambda$5 implements Runnable {
    private final ChangeBioActivity arg$1;
    private final AlertDialog arg$2;
    private final TL_error arg$3;
    private final TL_account_updateProfile arg$4;

    ChangeBioActivity$$Lambda$5(ChangeBioActivity changeBioActivity, AlertDialog alertDialog, TL_error tL_error, TL_account_updateProfile tL_account_updateProfile) {
        this.arg$1 = changeBioActivity;
        this.arg$2 = alertDialog;
        this.arg$3 = tL_error;
        this.arg$4 = tL_account_updateProfile;
    }

    public void run() {
        this.arg$1.lambda$null$3$ChangeBioActivity(this.arg$2, this.arg$3, this.arg$4);
    }
}
