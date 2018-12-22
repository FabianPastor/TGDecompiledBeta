package org.telegram.p005ui;

import org.telegram.p005ui.ActionBar.AlertDialog;

/* renamed from: org.telegram.ui.ChatActivity$$Lambda$59 */
final /* synthetic */ class ChatActivity$$Lambda$59 implements Runnable {
    private final AlertDialog[] arg$1;

    ChatActivity$$Lambda$59(AlertDialog[] alertDialogArr) {
        this.arg$1 = alertDialogArr;
    }

    public void run() {
        ChatActivity.lambda$processSelectedOption$76$ChatActivity(this.arg$1);
    }
}
