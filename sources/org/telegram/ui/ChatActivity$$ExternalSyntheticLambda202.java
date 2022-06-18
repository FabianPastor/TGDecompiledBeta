package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.Cells.ChatMessageCell;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda202 implements Runnable {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ AlertDialog[] f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ ChatMessageCell f$4;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda202(ChatActivity chatActivity, AlertDialog[] alertDialogArr, TLObject tLObject, String str, ChatMessageCell chatMessageCell) {
        this.f$0 = chatActivity;
        this.f$1 = alertDialogArr;
        this.f$2 = tLObject;
        this.f$3 = str;
        this.f$4 = chatMessageCell;
    }

    public final void run() {
        this.f$0.lambda$didPressMessageUrl$239(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
