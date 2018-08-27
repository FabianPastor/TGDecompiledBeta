package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

final /* synthetic */ class LaunchActivity$$Lambda$10 implements RequestDelegate {
    private final LaunchActivity arg$1;
    private final int arg$2;
    private final AlertDialog arg$3;

    LaunchActivity$$Lambda$10(LaunchActivity launchActivity, int i, AlertDialog alertDialog) {
        this.arg$1 = launchActivity;
        this.arg$2 = i;
        this.arg$3 = alertDialog;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$runLinkRequest$17$LaunchActivity(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
