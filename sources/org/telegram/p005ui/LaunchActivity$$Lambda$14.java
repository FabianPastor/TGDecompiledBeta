package org.telegram.p005ui;

import org.telegram.p005ui.ActionBar.AlertDialog;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.LaunchActivity$$Lambda$14 */
final /* synthetic */ class LaunchActivity$$Lambda$14 implements RequestDelegate {
    private final LaunchActivity arg$1;
    private final AlertDialog arg$2;

    LaunchActivity$$Lambda$14(LaunchActivity launchActivity, AlertDialog alertDialog) {
        this.arg$1 = launchActivity;
        this.arg$2 = alertDialog;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$runLinkRequest$26$LaunchActivity(this.arg$2, tLObject, tL_error);
    }
}
