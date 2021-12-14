package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class ChatLinkActivity$$ExternalSyntheticLambda16 implements RequestDelegate {
    public final /* synthetic */ ChatLinkActivity f$0;
    public final /* synthetic */ AlertDialog[] f$1;
    public final /* synthetic */ TLRPC$Chat f$2;
    public final /* synthetic */ BaseFragment f$3;

    public /* synthetic */ ChatLinkActivity$$ExternalSyntheticLambda16(ChatLinkActivity chatLinkActivity, AlertDialog[] alertDialogArr, TLRPC$Chat tLRPC$Chat, BaseFragment baseFragment) {
        this.f$0 = chatLinkActivity;
        this.f$1 = alertDialogArr;
        this.f$2 = tLRPC$Chat;
        this.f$3 = baseFragment;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$linkChat$13(this.f$1, this.f$2, this.f$3, tLObject, tLRPC$TL_error);
    }
}
