package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda48 implements Runnable {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ AlertDialog f$2;
    public final /* synthetic */ TLRPC$TL_error f$3;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda48(LaunchActivity launchActivity, TLObject tLObject, AlertDialog alertDialog, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0 = launchActivity;
        this.f$1 = tLObject;
        this.f$2 = alertDialog;
        this.f$3 = tLRPC$TL_error;
    }

    public final void run() {
        this.f$0.lambda$runLinkRequest$66(this.f$1, this.f$2, this.f$3);
    }
}
