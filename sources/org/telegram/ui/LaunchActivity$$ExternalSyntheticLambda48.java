package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda48 implements Runnable {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ TLRPC.TL_error f$2;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda48(LaunchActivity launchActivity, AlertDialog alertDialog, TLRPC.TL_error tL_error) {
        this.f$0 = launchActivity;
        this.f$1 = alertDialog;
        this.f$2 = tL_error;
    }

    public final void run() {
        this.f$0.m2361lambda$runLinkRequest$48$orgtelegramuiLaunchActivity(this.f$1, this.f$2);
    }
}
