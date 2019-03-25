package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_editMessage;
import org.telegram.ui.ActionBar.AlertDialog;

final /* synthetic */ class ChatActivity$$Lambda$62 implements RequestDelegate {
    private final ChatActivity arg$1;
    private final AlertDialog[] arg$2;
    private final TL_messages_editMessage arg$3;

    ChatActivity$$Lambda$62(ChatActivity chatActivity, AlertDialog[] alertDialogArr, TL_messages_editMessage tL_messages_editMessage) {
        this.arg$1 = chatActivity;
        this.arg$2 = alertDialogArr;
        this.arg$3 = tL_messages_editMessage;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$71$ChatActivity(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
