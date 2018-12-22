package org.telegram.p005ui;

import org.telegram.p005ui.ActionBar.AlertDialog;

/* renamed from: org.telegram.ui.ChatActivity$$Lambda$69 */
final /* synthetic */ class ChatActivity$$Lambda$69 implements Runnable {
    private final ChatActivity arg$1;
    private final AlertDialog[] arg$2;
    private final int arg$3;

    ChatActivity$$Lambda$69(ChatActivity chatActivity, AlertDialog[] alertDialogArr, int i) {
        this.arg$1 = chatActivity;
        this.arg$2 = alertDialogArr;
        this.arg$3 = i;
    }

    public void run() {
        this.arg$1.lambda$null$83$ChatActivity(this.arg$2, this.arg$3);
    }
}
