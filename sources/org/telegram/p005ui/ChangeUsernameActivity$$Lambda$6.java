package org.telegram.p005ui;

import org.telegram.p005ui.ActionBar.AlertDialog;
import org.telegram.tgnet.TLRPC.TL_account_updateUsername;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.ChangeUsernameActivity$$Lambda$6 */
final /* synthetic */ class ChangeUsernameActivity$$Lambda$6 implements Runnable {
    private final ChangeUsernameActivity arg$1;
    private final AlertDialog arg$2;
    private final TL_error arg$3;
    private final TL_account_updateUsername arg$4;

    ChangeUsernameActivity$$Lambda$6(ChangeUsernameActivity changeUsernameActivity, AlertDialog alertDialog, TL_error tL_error, TL_account_updateUsername tL_account_updateUsername) {
        this.arg$1 = changeUsernameActivity;
        this.arg$2 = alertDialog;
        this.arg$3 = tL_error;
        this.arg$4 = tL_account_updateUsername;
    }

    public void run() {
        this.arg$1.lambda$null$6$ChangeUsernameActivity(this.arg$2, this.arg$3, this.arg$4);
    }
}
