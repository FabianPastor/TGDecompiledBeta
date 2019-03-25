package org.telegram.ui;

import org.telegram.ui.ActionBar.AlertDialog;

final /* synthetic */ class ChatActivity$$Lambda$54 implements Runnable {
    private final AlertDialog[] arg$1;

    ChatActivity$$Lambda$54(AlertDialog[] alertDialogArr) {
        this.arg$1 = alertDialogArr;
    }

    public void run() {
        ChatActivity.lambda$processSelectedOption$66$ChatActivity(this.arg$1);
    }
}
