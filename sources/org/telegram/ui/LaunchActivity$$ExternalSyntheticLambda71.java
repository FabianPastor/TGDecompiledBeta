package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda71 implements RequestDelegate {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ AlertDialog f$3;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda71(LaunchActivity launchActivity, int i, String str, AlertDialog alertDialog) {
        this.f$0 = launchActivity;
        this.f$1 = i;
        this.f$2 = str;
        this.f$3 = alertDialog;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$runLinkRequest$30(this.f$1, this.f$2, this.f$3, tLObject, tLRPC$TL_error);
    }
}
