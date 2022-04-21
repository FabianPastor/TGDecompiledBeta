package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda40 implements Runnable {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ AlertDialog f$4;
    public final /* synthetic */ String f$5;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda40(LaunchActivity launchActivity, TLRPC.TL_error tL_error, TLObject tLObject, int i, AlertDialog alertDialog, String str) {
        this.f$0 = launchActivity;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
        this.f$3 = i;
        this.f$4 = alertDialog;
        this.f$5 = str;
    }

    public final void run() {
        this.f$0.m2354lambda$runLinkRequest$41$orgtelegramuiLaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
