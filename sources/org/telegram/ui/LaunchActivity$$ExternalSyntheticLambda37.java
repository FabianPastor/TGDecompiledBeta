package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda37 implements Runnable {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ AlertDialog f$2;
    public final /* synthetic */ TLRPC.TL_error f$3;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda37(LaunchActivity launchActivity, TLObject tLObject, AlertDialog alertDialog, TLRPC.TL_error tL_error) {
        this.f$0 = launchActivity;
        this.f$1 = tLObject;
        this.f$2 = alertDialog;
        this.f$3 = tL_error;
    }

    public final void run() {
        this.f$0.m2371lambda$runLinkRequest$58$orgtelegramuiLaunchActivity(this.f$1, this.f$2, this.f$3);
    }
}
