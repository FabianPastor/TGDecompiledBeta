package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

final /* synthetic */ class LaunchActivity$$Lambda$8 implements RequestDelegate {
    private final LaunchActivity arg$1;
    private final AlertDialog arg$2;
    private final String arg$3;
    private final int arg$4;
    private final String arg$5;
    private final String arg$6;
    private final Integer arg$7;

    LaunchActivity$$Lambda$8(LaunchActivity launchActivity, AlertDialog alertDialog, String str, int i, String str2, String str3, Integer num) {
        this.arg$1 = launchActivity;
        this.arg$2 = alertDialog;
        this.arg$3 = str;
        this.arg$4 = i;
        this.arg$5 = str2;
        this.arg$6 = str3;
        this.arg$7 = num;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$runLinkRequest$12$LaunchActivity(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, tLObject, tL_error);
    }
}
