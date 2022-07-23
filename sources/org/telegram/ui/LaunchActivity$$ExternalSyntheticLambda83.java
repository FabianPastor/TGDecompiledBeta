package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_wallPaper;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda83 implements RequestDelegate {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ TLRPC$TL_wallPaper f$2;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda83(LaunchActivity launchActivity, AlertDialog alertDialog, TLRPC$TL_wallPaper tLRPC$TL_wallPaper) {
        this.f$0 = launchActivity;
        this.f$1 = alertDialog;
        this.f$2 = tLRPC$TL_wallPaper;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$runLinkRequest$62(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
