package org.telegram.ui;

import java.util.HashMap;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_wallPaper;
import org.telegram.ui.ActionBar.AlertDialog;

final /* synthetic */ class LaunchActivity$$Lambda$48 implements Runnable {
    private final LaunchActivity arg$1;
    private final String arg$10;
    private final String arg$11;
    private final boolean arg$12;
    private final Integer arg$13;
    private final String arg$14;
    private final HashMap arg$15;
    private final String arg$16;
    private final String arg$17;
    private final String arg$18;
    private final TL_wallPaper arg$19;
    private final AlertDialog arg$2;
    private final TL_error arg$3;
    private final TLObject arg$4;
    private final int arg$5;
    private final String arg$6;
    private final String arg$7;
    private final String arg$8;
    private final String arg$9;

    LaunchActivity$$Lambda$48(LaunchActivity launchActivity, AlertDialog alertDialog, TL_error tL_error, TLObject tLObject, int i, String str, String str2, String str3, String str4, String str5, String str6, boolean z, Integer num, String str7, HashMap hashMap, String str8, String str9, String str10, TL_wallPaper tL_wallPaper) {
        this.arg$1 = launchActivity;
        this.arg$2 = alertDialog;
        this.arg$3 = tL_error;
        this.arg$4 = tLObject;
        this.arg$5 = i;
        this.arg$6 = str;
        this.arg$7 = str2;
        this.arg$8 = str3;
        this.arg$9 = str4;
        this.arg$10 = str5;
        this.arg$11 = str6;
        this.arg$12 = z;
        this.arg$13 = num;
        this.arg$14 = str7;
        this.arg$15 = hashMap;
        this.arg$16 = str8;
        this.arg$17 = str9;
        this.arg$18 = str10;
        this.arg$19 = tL_wallPaper;
    }

    public void run() {
        this.arg$1.lambda$null$14$LaunchActivity(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8, this.arg$9, this.arg$10, this.arg$11, this.arg$12, this.arg$13, this.arg$14, this.arg$15, this.arg$16, this.arg$17, this.arg$18, this.arg$19);
    }
}
