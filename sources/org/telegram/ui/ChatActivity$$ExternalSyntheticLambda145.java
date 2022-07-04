package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.Cells.ChatMessageCell;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda145 implements RequestDelegate {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ AlertDialog[] f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ ChatMessageCell f$3;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda145(ChatActivity chatActivity, AlertDialog[] alertDialogArr, String str, ChatMessageCell chatMessageCell) {
        this.f$0 = chatActivity;
        this.f$1 = alertDialogArr;
        this.f$2 = str;
        this.f$3 = chatMessageCell;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3001lambda$didPressMessageUrl$240$orgtelegramuiChatActivity(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
