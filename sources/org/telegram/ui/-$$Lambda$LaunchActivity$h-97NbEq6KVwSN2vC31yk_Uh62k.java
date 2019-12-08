package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_wallPaper;
import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LaunchActivity$h-97NbEq6KVwSN2vCLASSNAMEyk_Uh62k implements RequestDelegate {
    private final /* synthetic */ LaunchActivity f$0;
    private final /* synthetic */ AlertDialog f$1;
    private final /* synthetic */ TL_wallPaper f$2;

    public /* synthetic */ -$$Lambda$LaunchActivity$h-97NbEq6KVwSN2vCLASSNAMEyk_Uh62k(LaunchActivity launchActivity, AlertDialog alertDialog, TL_wallPaper tL_wallPaper) {
        this.f$0 = launchActivity;
        this.f$1 = alertDialog;
        this.f$2 = tL_wallPaper;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$runLinkRequest$31$LaunchActivity(this.f$1, this.f$2, tLObject, tL_error);
    }
}
