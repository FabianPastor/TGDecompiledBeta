package org.telegram.ui;

import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.AlertDialog;

final /* synthetic */ class ChangeUsernameActivity$$Lambda$5 implements Runnable {
    private final ChangeUsernameActivity arg$1;
    private final AlertDialog arg$2;
    private final User arg$3;

    ChangeUsernameActivity$$Lambda$5(ChangeUsernameActivity changeUsernameActivity, AlertDialog alertDialog, User user) {
        this.arg$1 = changeUsernameActivity;
        this.arg$2 = alertDialog;
        this.arg$3 = user;
    }

    public void run() {
        this.arg$1.lambda$null$5$ChangeUsernameActivity(this.arg$2, this.arg$3);
    }
}