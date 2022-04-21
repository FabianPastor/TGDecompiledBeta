package org.telegram.ui;

import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.Cells.ChatMessageCell;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda101 implements Runnable {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ AlertDialog[] f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ ChatMessageCell f$3;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda101(ChatActivity chatActivity, AlertDialog[] alertDialogArr, int i, ChatMessageCell chatMessageCell) {
        this.f$0 = chatActivity;
        this.f$1 = alertDialogArr;
        this.f$2 = i;
        this.f$3 = chatMessageCell;
    }

    public final void run() {
        this.f$0.m1722lambda$didPressMessageUrl$227$orgtelegramuiChatActivity(this.f$1, this.f$2, this.f$3);
    }
}
