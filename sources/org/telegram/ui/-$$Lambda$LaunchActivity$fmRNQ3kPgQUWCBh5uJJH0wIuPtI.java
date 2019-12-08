package org.telegram.ui;

import java.util.HashMap;
import org.telegram.tgnet.TLRPC.TL_wallPaper;
import org.telegram.ui.Components.AlertsCreator.AccountSelectDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LaunchActivity$fmRNQ3kPgQUWCBh5uJJH0wIuPtI implements AccountSelectDelegate {
    private final /* synthetic */ LaunchActivity f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ Integer f$10;
    private final /* synthetic */ String f$11;
    private final /* synthetic */ HashMap f$12;
    private final /* synthetic */ String f$13;
    private final /* synthetic */ String f$14;
    private final /* synthetic */ String f$15;
    private final /* synthetic */ TL_wallPaper f$16;
    private final /* synthetic */ String f$17;
    private final /* synthetic */ String f$2;
    private final /* synthetic */ String f$3;
    private final /* synthetic */ String f$4;
    private final /* synthetic */ String f$5;
    private final /* synthetic */ String f$6;
    private final /* synthetic */ String f$7;
    private final /* synthetic */ boolean f$8;
    private final /* synthetic */ Integer f$9;

    public /* synthetic */ -$$Lambda$LaunchActivity$fmRNQ3kPgQUWCBh5uJJH0wIuPtI(LaunchActivity launchActivity, int i, String str, String str2, String str3, String str4, String str5, String str6, boolean z, Integer num, Integer num2, String str7, HashMap hashMap, String str8, String str9, String str10, TL_wallPaper tL_wallPaper, String str11) {
        this.f$0 = launchActivity;
        this.f$1 = i;
        this.f$2 = str;
        this.f$3 = str2;
        this.f$4 = str3;
        this.f$5 = str4;
        this.f$6 = str5;
        this.f$7 = str6;
        this.f$8 = z;
        this.f$9 = num;
        this.f$10 = num2;
        this.f$11 = str7;
        this.f$12 = hashMap;
        this.f$13 = str8;
        this.f$14 = str9;
        this.f$15 = str10;
        this.f$16 = tL_wallPaper;
        this.f$17 = str11;
    }

    public final void didSelectAccount(int i) {
        int i2 = i;
        LaunchActivity launchActivity = this.f$0;
        LaunchActivity launchActivity2 = launchActivity;
        launchActivity2.lambda$runLinkRequest$10$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, this.f$13, this.f$14, this.f$15, this.f$16, this.f$17, i2);
    }
}
