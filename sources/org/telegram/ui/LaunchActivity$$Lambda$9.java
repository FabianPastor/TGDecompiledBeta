package org.telegram.ui;

import java.util.HashMap;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

final /* synthetic */ class LaunchActivity$$Lambda$9 implements RequestDelegate {
    private final LaunchActivity arg$1;
    private final boolean arg$10;
    private final Integer arg$11;
    private final String arg$12;
    private final String[] arg$13;
    private final HashMap arg$14;
    private final String arg$15;
    private final AlertDialog arg$2;
    private final int arg$3;
    private final String arg$4;
    private final String arg$5;
    private final String arg$6;
    private final String arg$7;
    private final String arg$8;
    private final String arg$9;

    LaunchActivity$$Lambda$9(LaunchActivity launchActivity, AlertDialog alertDialog, int i, String str, String str2, String str3, String str4, String str5, String str6, boolean z, Integer num, String str7, String[] strArr, HashMap hashMap, String str8) {
        this.arg$1 = launchActivity;
        this.arg$2 = alertDialog;
        this.arg$3 = i;
        this.arg$4 = str;
        this.arg$5 = str2;
        this.arg$6 = str3;
        this.arg$7 = str4;
        this.arg$8 = str5;
        this.arg$9 = str6;
        this.arg$10 = z;
        this.arg$11 = num;
        this.arg$12 = str7;
        this.arg$13 = strArr;
        this.arg$14 = hashMap;
        this.arg$15 = str8;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$runLinkRequest$15$LaunchActivity(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8, this.arg$9, this.arg$10, this.arg$11, this.arg$12, this.arg$13, this.arg$14, this.arg$15, tLObject, tL_error);
    }
}
