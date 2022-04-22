package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class ChatLinkActivity$$ExternalSyntheticLambda15 implements RequestDelegate {
    public final /* synthetic */ ChatLinkActivity f$0;
    public final /* synthetic */ AlertDialog[] f$1;

    public /* synthetic */ ChatLinkActivity$$ExternalSyntheticLambda15(ChatLinkActivity chatLinkActivity, AlertDialog[] alertDialogArr) {
        this.f$0 = chatLinkActivity;
        this.f$1 = alertDialogArr;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$createView$2(this.f$1, tLObject, tLRPC$TL_error);
    }
}
