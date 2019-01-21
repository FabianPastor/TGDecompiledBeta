package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

final /* synthetic */ class LaunchActivity$$Lambda$40 implements Runnable {
    private final LaunchActivity arg$1;
    private final AlertDialog arg$2;
    private final TLObject arg$3;
    private final int arg$4;
    private final TL_error arg$5;

    LaunchActivity$$Lambda$40(LaunchActivity launchActivity, AlertDialog alertDialog, TLObject tLObject, int i, TL_error tL_error) {
        this.arg$1 = launchActivity;
        this.arg$2 = alertDialog;
        this.arg$3 = tLObject;
        this.arg$4 = i;
        this.arg$5 = tL_error;
    }

    public void run() {
        this.arg$1.lambda$null$27$LaunchActivity(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
