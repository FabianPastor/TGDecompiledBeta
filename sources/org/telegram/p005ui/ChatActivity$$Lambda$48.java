package org.telegram.p005ui;

import org.telegram.p005ui.ActionBar.AlertDialog;

/* renamed from: org.telegram.ui.ChatActivity$$Lambda$48 */
final /* synthetic */ class ChatActivity$$Lambda$48 implements Runnable {
    private final ChatActivity arg$1;
    private final AlertDialog[] arg$2;
    private final int arg$3;

    ChatActivity$$Lambda$48(ChatActivity chatActivity, AlertDialog[] alertDialogArr, int i) {
        this.arg$1 = chatActivity;
        this.arg$2 = alertDialogArr;
        this.arg$3 = i;
    }

    public void run() {
        this.arg$1.lambda$createDeleteMessagesAlert$62$ChatActivity(this.arg$2, this.arg$3);
    }
}
