package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda75 implements RequestDelegate {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ TLRPC.TL_wallPaper f$2;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda75(LaunchActivity launchActivity, AlertDialog alertDialog, TLRPC.TL_wallPaper tL_wallPaper) {
        this.f$0 = launchActivity;
        this.f$1 = alertDialog;
        this.f$2 = tL_wallPaper;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2369lambda$runLinkRequest$56$orgtelegramuiLaunchActivity(this.f$1, this.f$2, tLObject, tL_error);
    }
}
