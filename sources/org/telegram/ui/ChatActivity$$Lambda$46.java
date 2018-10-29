package org.telegram.ui;

import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessageObject.GroupedMessages;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

final /* synthetic */ class ChatActivity$$Lambda$46 implements RequestDelegate {
    private final ChatActivity arg$1;
    private final AlertDialog[] arg$2;
    private final MessageObject arg$3;
    private final GroupedMessages arg$4;

    ChatActivity$$Lambda$46(ChatActivity chatActivity, AlertDialog[] alertDialogArr, MessageObject messageObject, GroupedMessages groupedMessages) {
        this.arg$1 = chatActivity;
        this.arg$2 = alertDialogArr;
        this.arg$3 = messageObject;
        this.arg$4 = groupedMessages;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$createDeleteMessagesAlert$59$ChatActivity(this.arg$2, this.arg$3, this.arg$4, tLObject, tL_error);
    }
}
