package org.telegram.p005ui;

import org.telegram.p005ui.ActionBar.AlertDialog;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_updateUsername;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.ChangeUsernameActivity$$Lambda$3 */
final /* synthetic */ class ChangeUsernameActivity$$Lambda$3 implements RequestDelegate {
    private final ChangeUsernameActivity arg$1;
    private final AlertDialog arg$2;
    private final TL_account_updateUsername arg$3;

    ChangeUsernameActivity$$Lambda$3(ChangeUsernameActivity changeUsernameActivity, AlertDialog alertDialog, TL_account_updateUsername tL_account_updateUsername) {
        this.arg$1 = changeUsernameActivity;
        this.arg$2 = alertDialog;
        this.arg$3 = tL_account_updateUsername;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$saveName$7$ChangeUsernameActivity(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
