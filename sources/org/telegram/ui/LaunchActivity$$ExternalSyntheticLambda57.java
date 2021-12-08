package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda57 implements RequestDelegate {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ String f$10;
    public final /* synthetic */ int f$11;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ Integer f$4;
    public final /* synthetic */ Integer f$5;
    public final /* synthetic */ Integer f$6;
    public final /* synthetic */ int[] f$7;
    public final /* synthetic */ AlertDialog f$8;
    public final /* synthetic */ String f$9;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda57(LaunchActivity launchActivity, String str, String str2, int i, Integer num, Integer num2, Integer num3, int[] iArr, AlertDialog alertDialog, String str3, String str4, int i2) {
        this.f$0 = launchActivity;
        this.f$1 = str;
        this.f$2 = str2;
        this.f$3 = i;
        this.f$4 = num;
        this.f$5 = num2;
        this.f$6 = num3;
        this.f$7 = iArr;
        this.f$8 = alertDialog;
        this.f$9 = str3;
        this.f$10 = str4;
        this.f$11 = i2;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3095lambda$runLinkRequest$28$orgtelegramuiLaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, tLObject, tL_error);
    }
}
