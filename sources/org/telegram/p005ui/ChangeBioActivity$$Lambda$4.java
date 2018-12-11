package org.telegram.p005ui;

import org.telegram.p005ui.ActionBar.AlertDialog;
import org.telegram.tgnet.TLRPC.TL_userFull;
import org.telegram.tgnet.TLRPC.User;

/* renamed from: org.telegram.ui.ChangeBioActivity$$Lambda$4 */
final /* synthetic */ class ChangeBioActivity$$Lambda$4 implements Runnable {
    private final ChangeBioActivity arg$1;
    private final AlertDialog arg$2;
    private final TL_userFull arg$3;
    private final String arg$4;
    private final User arg$5;

    ChangeBioActivity$$Lambda$4(ChangeBioActivity changeBioActivity, AlertDialog alertDialog, TL_userFull tL_userFull, String str, User user) {
        this.arg$1 = changeBioActivity;
        this.arg$2 = alertDialog;
        this.arg$3 = tL_userFull;
        this.arg$4 = str;
        this.arg$5 = user;
    }

    public void run() {
        this.arg$1.lambda$null$2$ChangeBioActivity(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
