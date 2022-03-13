package org.telegram.ui;

import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda49 implements Runnable {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ TLRPC$TL_error f$2;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda49(LaunchActivity launchActivity, AlertDialog alertDialog, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0 = launchActivity;
        this.f$1 = alertDialog;
        this.f$2 = tLRPC$TL_error;
    }

    public final void run() {
        this.f$0.lambda$runLinkRequest$41(this.f$1, this.f$2);
    }
}
