package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda69 implements RequestDelegate {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ int[] f$10;
    public final /* synthetic */ AlertDialog f$11;
    public final /* synthetic */ String f$12;
    public final /* synthetic */ String f$13;
    public final /* synthetic */ String f$14;
    public final /* synthetic */ int f$15;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ String f$5;
    public final /* synthetic */ String f$6;
    public final /* synthetic */ Integer f$7;
    public final /* synthetic */ Integer f$8;
    public final /* synthetic */ Integer f$9;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda69(LaunchActivity launchActivity, String str, String str2, String str3, int i, String str4, String str5, Integer num, Integer num2, Integer num3, int[] iArr, AlertDialog alertDialog, String str6, String str7, String str8, int i2) {
        this.f$0 = launchActivity;
        this.f$1 = str;
        this.f$2 = str2;
        this.f$3 = str3;
        this.f$4 = i;
        this.f$5 = str4;
        this.f$6 = str5;
        this.f$7 = num;
        this.f$8 = num2;
        this.f$9 = num3;
        this.f$10 = iArr;
        this.f$11 = alertDialog;
        this.f$12 = str6;
        this.f$13 = str7;
        this.f$14 = str8;
        this.f$15 = i2;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        LaunchActivity launchActivity = this.f$0;
        LaunchActivity launchActivity2 = launchActivity;
        launchActivity2.m2353lambda$runLinkRequest$39$orgtelegramuiLaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, this.f$13, this.f$14, this.f$15, tLObject, tL_error);
    }
}
