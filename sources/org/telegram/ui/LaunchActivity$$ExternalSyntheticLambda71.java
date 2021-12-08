package org.telegram.ui;

import java.util.HashMap;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.AlertsCreator;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda71 implements AlertsCreator.AccountSelectDelegate {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ Long f$10;
    public final /* synthetic */ Integer f$11;
    public final /* synthetic */ Integer f$12;
    public final /* synthetic */ String f$13;
    public final /* synthetic */ HashMap f$14;
    public final /* synthetic */ String f$15;
    public final /* synthetic */ String f$16;
    public final /* synthetic */ String f$17;
    public final /* synthetic */ String f$18;
    public final /* synthetic */ TLRPC.TL_wallPaper f$19;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ String f$20;
    public final /* synthetic */ String f$21;
    public final /* synthetic */ int f$22;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ String f$4;
    public final /* synthetic */ String f$5;
    public final /* synthetic */ String f$6;
    public final /* synthetic */ String f$7;
    public final /* synthetic */ boolean f$8;
    public final /* synthetic */ Integer f$9;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda71(LaunchActivity launchActivity, int i, String str, String str2, String str3, String str4, String str5, String str6, boolean z, Integer num, Long l, Integer num2, Integer num3, String str7, HashMap hashMap, String str8, String str9, String str10, String str11, TLRPC.TL_wallPaper tL_wallPaper, String str12, String str13, int i2) {
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
        this.f$10 = l;
        this.f$11 = num2;
        this.f$12 = num3;
        this.f$13 = str7;
        this.f$14 = hashMap;
        this.f$15 = str8;
        this.f$16 = str9;
        this.f$17 = str10;
        this.f$18 = str11;
        this.f$19 = tL_wallPaper;
        this.f$20 = str12;
        this.f$21 = str13;
        this.f$22 = i2;
    }

    public final void didSelectAccount(int i) {
        int i2 = i;
        LaunchActivity launchActivity = this.f$0;
        LaunchActivity launchActivity2 = launchActivity;
        launchActivity2.m3091lambda$runLinkRequest$24$orgtelegramuiLaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, this.f$13, this.f$14, this.f$15, this.f$16, this.f$17, this.f$18, this.f$19, this.f$20, this.f$21, this.f$22, i2);
    }
}
