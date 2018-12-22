package org.telegram.p005ui;

import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessageObject.GroupedMessages;
import org.telegram.p005ui.ActionBar.AlertDialog;
import org.telegram.tgnet.TLObject;

/* renamed from: org.telegram.ui.ChatActivity$$Lambda$78 */
final /* synthetic */ class ChatActivity$$Lambda$78 implements Runnable {
    private final ChatActivity arg$1;
    private final AlertDialog[] arg$2;
    private final TLObject arg$3;
    private final MessageObject arg$4;
    private final GroupedMessages arg$5;

    ChatActivity$$Lambda$78(ChatActivity chatActivity, AlertDialog[] alertDialogArr, TLObject tLObject, MessageObject messageObject, GroupedMessages groupedMessages) {
        this.arg$1 = chatActivity;
        this.arg$2 = alertDialogArr;
        this.arg$3 = tLObject;
        this.arg$4 = messageObject;
        this.arg$5 = groupedMessages;
    }

    public void run() {
        this.arg$1.lambda$null$59$ChatActivity(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
