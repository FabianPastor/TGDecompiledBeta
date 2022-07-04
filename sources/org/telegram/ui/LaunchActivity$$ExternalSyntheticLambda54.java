package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda54 implements Runnable {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ TLRPC.TL_wallPaper f$3;
    public final /* synthetic */ TLRPC.TL_error f$4;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda54(LaunchActivity launchActivity, AlertDialog alertDialog, TLObject tLObject, TLRPC.TL_wallPaper tL_wallPaper, TLRPC.TL_error tL_error) {
        this.f$0 = launchActivity;
        this.f$1 = alertDialog;
        this.f$2 = tLObject;
        this.f$3 = tL_wallPaper;
        this.f$4 = tL_error;
    }

    public final void run() {
        this.f$0.m3691lambda$runLinkRequest$60$orgtelegramuiLaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
