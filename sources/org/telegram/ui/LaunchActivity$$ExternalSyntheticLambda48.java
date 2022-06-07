package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda48 implements Runnable {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ String f$4;
    public final /* synthetic */ AlertDialog f$5;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda48(LaunchActivity launchActivity, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, int i, String str, AlertDialog alertDialog) {
        this.f$0 = launchActivity;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = tLObject;
        this.f$3 = i;
        this.f$4 = str;
        this.f$5 = alertDialog;
    }

    public final void run() {
        this.f$0.lambda$runLinkRequest$29(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
