package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class ChatLinkActivity$$ExternalSyntheticLambda6 implements RequestDelegate {
    public final /* synthetic */ ChatLinkActivity f$0;
    public final /* synthetic */ AlertDialog[] f$1;

    public /* synthetic */ ChatLinkActivity$$ExternalSyntheticLambda6(ChatLinkActivity chatLinkActivity, AlertDialog[] alertDialogArr) {
        this.f$0 = chatLinkActivity;
        this.f$1 = alertDialogArr;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m1942lambda$createView$2$orgtelegramuiChatLinkActivity(this.f$1, tLObject, tL_error);
    }
}
