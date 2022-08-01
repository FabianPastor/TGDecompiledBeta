package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda74 implements RequestDelegate {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ AlertDialog f$2;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda74(LaunchActivity launchActivity, int i, AlertDialog alertDialog) {
        this.f$0 = launchActivity;
        this.f$1 = i;
        this.f$2 = alertDialog;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$runLinkRequest$51(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
