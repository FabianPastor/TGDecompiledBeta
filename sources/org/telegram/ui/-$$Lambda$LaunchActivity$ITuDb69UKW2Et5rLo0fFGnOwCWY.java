package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LaunchActivity$ITuDb69UKW2Et5rLo0fFGnOwCWY implements RequestDelegate {
    private final /* synthetic */ LaunchActivity f$0;
    private final /* synthetic */ AlertDialog f$1;

    public /* synthetic */ -$$Lambda$LaunchActivity$ITuDb69UKW2Et5rLo0fFGnOwCWY(LaunchActivity launchActivity, AlertDialog alertDialog) {
        this.f$0 = launchActivity;
        this.f$1 = alertDialog;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$runLinkRequest$33$LaunchActivity(this.f$1, tLObject, tL_error);
    }
}
