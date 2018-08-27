package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.AlertDialog;

final /* synthetic */ class LaunchActivity$$Lambda$38 implements Runnable {
    private final LaunchActivity arg$1;
    private final AlertDialog arg$2;
    private final TLObject arg$3;

    LaunchActivity$$Lambda$38(LaunchActivity launchActivity, AlertDialog alertDialog, TLObject tLObject) {
        this.arg$1 = launchActivity;
        this.arg$2 = alertDialog;
        this.arg$3 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$23$LaunchActivity(this.arg$2, this.arg$3);
    }
}
