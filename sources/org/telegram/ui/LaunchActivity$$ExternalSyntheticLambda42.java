package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda42 implements Runnable {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ TLRPC.TL_error f$2;
    public final /* synthetic */ TLObject f$3;
    public final /* synthetic */ int f$4;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda42(LaunchActivity launchActivity, AlertDialog alertDialog, TLRPC.TL_error tL_error, TLObject tLObject, int i) {
        this.f$0 = launchActivity;
        this.f$1 = alertDialog;
        this.f$2 = tL_error;
        this.f$3 = tLObject;
        this.f$4 = i;
    }

    public final void run() {
        this.f$0.m3098lambda$runLinkRequest$32$orgtelegramuiLaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
