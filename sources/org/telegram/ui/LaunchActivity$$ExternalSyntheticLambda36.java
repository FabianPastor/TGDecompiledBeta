package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda36 implements Runnable {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ AlertDialog f$10;
    public final /* synthetic */ String f$11;
    public final /* synthetic */ String f$12;
    public final /* synthetic */ int f$13;
    public final /* synthetic */ TLRPC$TL_error f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ String f$4;
    public final /* synthetic */ int f$5;
    public final /* synthetic */ Integer f$6;
    public final /* synthetic */ Integer f$7;
    public final /* synthetic */ Integer f$8;
    public final /* synthetic */ int[] f$9;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda36(LaunchActivity launchActivity, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error, String str, String str2, int i, Integer num, Integer num2, Integer num3, int[] iArr, AlertDialog alertDialog, String str3, String str4, int i2) {
        this.f$0 = launchActivity;
        this.f$1 = tLObject;
        this.f$2 = tLRPC$TL_error;
        this.f$3 = str;
        this.f$4 = str2;
        this.f$5 = i;
        this.f$6 = num;
        this.f$7 = num2;
        this.f$8 = num3;
        this.f$9 = iArr;
        this.f$10 = alertDialog;
        this.f$11 = str3;
        this.f$12 = str4;
        this.f$13 = i2;
    }

    public final void run() {
        this.f$0.lambda$runLinkRequest$27(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, this.f$13);
    }
}
