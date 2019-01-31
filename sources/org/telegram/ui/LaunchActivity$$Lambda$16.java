package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_wallPaper;
import org.telegram.ui.ActionBar.AlertDialog;

final /* synthetic */ class LaunchActivity$$Lambda$16 implements RequestDelegate {
    private final LaunchActivity arg$1;
    private final AlertDialog arg$2;
    private final TL_wallPaper arg$3;

    LaunchActivity$$Lambda$16(LaunchActivity launchActivity, AlertDialog alertDialog, TL_wallPaper tL_wallPaper) {
        this.arg$1 = launchActivity;
        this.arg$2 = alertDialog;
        this.arg$3 = tL_wallPaper;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$runLinkRequest$29$LaunchActivity(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
