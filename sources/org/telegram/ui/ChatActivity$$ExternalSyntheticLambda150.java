package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda150 implements RequestDelegate {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ AlertDialog[] f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda150(ChatActivity chatActivity, AlertDialog[] alertDialogArr, String str) {
        this.f$0 = chatActivity;
        this.f$1 = alertDialogArr;
        this.f$2 = str;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$didPressMessageUrl$157(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
