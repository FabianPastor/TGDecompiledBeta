package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda54 implements RequestDelegate {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ AlertDialog f$2;
    public final /* synthetic */ String f$3;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda54(LaunchActivity launchActivity, int i, AlertDialog alertDialog, String str) {
        this.f$0 = launchActivity;
        this.f$1 = i;
        this.f$2 = alertDialog;
        this.f$3 = str;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3097lambda$runLinkRequest$31$orgtelegramuiLaunchActivity(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
