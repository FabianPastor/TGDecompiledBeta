package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda55 implements Runnable {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ TLRPC$TL_error f$3;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda55(LaunchActivity launchActivity, AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0 = launchActivity;
        this.f$1 = alertDialog;
        this.f$2 = tLObject;
        this.f$3 = tLRPC$TL_error;
    }

    public final void run() {
        this.f$0.lambda$runLinkRequest$59(this.f$1, this.f$2, this.f$3);
    }
}
