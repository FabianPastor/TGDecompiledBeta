package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda129 implements RequestDelegate {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ AlertDialog[] f$1;
    public final /* synthetic */ TLRPC.TL_messages_editMessage f$2;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda129(ChatActivity chatActivity, AlertDialog[] alertDialogArr, TLRPC.TL_messages_editMessage tL_messages_editMessage) {
        this.f$0 = chatActivity;
        this.f$1 = alertDialogArr;
        this.f$2 = tL_messages_editMessage;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m1789lambda$processSelectedOption$197$orgtelegramuiChatActivity(this.f$1, this.f$2, tLObject, tL_error);
    }
}
