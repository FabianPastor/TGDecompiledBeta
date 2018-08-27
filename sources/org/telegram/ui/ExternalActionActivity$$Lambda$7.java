package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

final /* synthetic */ class ExternalActionActivity$$Lambda$7 implements Runnable {
    private final ExternalActionActivity arg$1;
    private final AlertDialog arg$2;
    private final TL_error arg$3;

    ExternalActionActivity$$Lambda$7(ExternalActionActivity externalActionActivity, AlertDialog alertDialog, TL_error tL_error) {
        this.arg$1 = externalActionActivity;
        this.arg$2 = alertDialog;
        this.arg$3 = tL_error;
    }

    public void run() {
        this.arg$1.lambda$null$8$ExternalActionActivity(this.arg$2, this.arg$3);
    }
}
