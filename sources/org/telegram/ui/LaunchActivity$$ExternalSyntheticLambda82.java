package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda82 implements RequestDelegate {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ AlertDialog f$1;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda82(LaunchActivity launchActivity, AlertDialog alertDialog) {
        this.f$0 = launchActivity;
        this.f$1 = alertDialog;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3695lambda$runLinkRequest$64$orgtelegramuiLaunchActivity(this.f$1, tLObject, tL_error);
    }
}
