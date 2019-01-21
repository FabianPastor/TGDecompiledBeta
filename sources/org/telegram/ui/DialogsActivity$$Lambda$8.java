package org.telegram.ui;

import org.telegram.tgnet.TLRPC.Chat;

final /* synthetic */ class DialogsActivity$$Lambda$8 implements Runnable {
    private final DialogsActivity arg$1;
    private final Chat arg$2;
    private final long arg$3;

    DialogsActivity$$Lambda$8(DialogsActivity dialogsActivity, Chat chat, long j) {
        this.arg$1 = dialogsActivity;
        this.arg$2 = chat;
        this.arg$3 = j;
    }

    public void run() {
        this.arg$1.lambda$didReceivedNotification$8$DialogsActivity(this.arg$2, this.arg$3);
    }
}
