package org.telegram.p005ui;

import org.telegram.p005ui.ActionBar.AlertDialog;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.LaunchActivity$$Lambda$39 */
final /* synthetic */ class LaunchActivity$$Lambda$39 implements Runnable {
    private final LaunchActivity arg$1;
    private final AlertDialog arg$2;
    private final TLObject arg$3;
    private final TL_error arg$4;

    LaunchActivity$$Lambda$39(LaunchActivity launchActivity, AlertDialog alertDialog, TLObject tLObject, TL_error tL_error) {
        this.arg$1 = launchActivity;
        this.arg$2 = alertDialog;
        this.arg$3 = tLObject;
        this.arg$4 = tL_error;
    }

    public void run() {
        this.arg$1.lambda$null$25$LaunchActivity(this.arg$2, this.arg$3, this.arg$4);
    }
}
