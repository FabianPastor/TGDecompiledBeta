package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda69 implements RequestDelegate {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ int[] f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ AlertDialog f$3;
    public final /* synthetic */ Integer f$4;
    public final /* synthetic */ Integer f$5;
    public final /* synthetic */ Integer f$6;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda69(LaunchActivity launchActivity, int[] iArr, int i, AlertDialog alertDialog, Integer num, Integer num2, Integer num3) {
        this.f$0 = launchActivity;
        this.f$1 = iArr;
        this.f$2 = i;
        this.f$3 = alertDialog;
        this.f$4 = num;
        this.f$5 = num2;
        this.f$6 = num3;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$runLinkRequest$50(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, tLObject, tLRPC$TL_error);
    }
}