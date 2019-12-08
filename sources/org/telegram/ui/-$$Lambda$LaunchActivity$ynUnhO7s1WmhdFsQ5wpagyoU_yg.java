package org.telegram.ui;

import java.util.HashMap;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_wallPaper;
import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LaunchActivity$ynUnhO7s1WmhdFsQ5wpagyoU_yg implements RequestDelegate {
    private final /* synthetic */ LaunchActivity f$0;
    private final /* synthetic */ AlertDialog f$1;
    private final /* synthetic */ Integer f$10;
    private final /* synthetic */ Integer f$11;
    private final /* synthetic */ String f$12;
    private final /* synthetic */ HashMap f$13;
    private final /* synthetic */ String f$14;
    private final /* synthetic */ String f$15;
    private final /* synthetic */ String f$16;
    private final /* synthetic */ TL_wallPaper f$17;
    private final /* synthetic */ String f$18;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ String f$3;
    private final /* synthetic */ String f$4;
    private final /* synthetic */ String f$5;
    private final /* synthetic */ String f$6;
    private final /* synthetic */ String f$7;
    private final /* synthetic */ String f$8;
    private final /* synthetic */ boolean f$9;

    public /* synthetic */ -$$Lambda$LaunchActivity$ynUnhO7s1WmhdFsQ5wpagyoU_yg(LaunchActivity launchActivity, AlertDialog alertDialog, int i, String str, String str2, String str3, String str4, String str5, String str6, boolean z, Integer num, Integer num2, String str7, HashMap hashMap, String str8, String str9, String str10, TL_wallPaper tL_wallPaper, String str11) {
        this.f$0 = launchActivity;
        this.f$1 = alertDialog;
        this.f$2 = i;
        this.f$3 = str;
        this.f$4 = str2;
        this.f$5 = str3;
        this.f$6 = str4;
        this.f$7 = str5;
        this.f$8 = str6;
        this.f$9 = z;
        this.f$10 = num;
        this.f$11 = num2;
        this.f$12 = str7;
        this.f$13 = hashMap;
        this.f$14 = str8;
        this.f$15 = str9;
        this.f$16 = str10;
        this.f$17 = tL_wallPaper;
        this.f$18 = str11;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        TLObject tLObject2 = tLObject;
        TL_error tL_error2 = tL_error;
        LaunchActivity launchActivity = this.f$0;
        LaunchActivity launchActivity2 = launchActivity;
        launchActivity2.lambda$runLinkRequest$17$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, this.f$13, this.f$14, this.f$15, this.f$16, this.f$17, this.f$18, tLObject2, tL_error2);
    }
}
