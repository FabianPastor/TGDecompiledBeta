package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

final /* synthetic */ class LaunchActivity$$Lambda$40 implements Runnable {
    private final LaunchActivity arg$1;
    private final AlertDialog arg$2;
    private final TL_error arg$3;

    LaunchActivity$$Lambda$40(LaunchActivity launchActivity, AlertDialog alertDialog, TL_error tL_error) {
        this.arg$1 = launchActivity;
        this.arg$2 = alertDialog;
        this.arg$3 = tL_error;
    }

    public void run() {
        this.arg$1.lambda$null$21$LaunchActivity(this.arg$2, this.arg$3);
    }
}
