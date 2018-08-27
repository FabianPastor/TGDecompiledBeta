package org.telegram.ui;

import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.MessagesStorage;

final /* synthetic */ class ChatActivity$$Lambda$2 implements Runnable {
    private final ChatActivity arg$1;
    private final MessagesStorage arg$2;
    private final int arg$3;
    private final CountDownLatch arg$4;

    ChatActivity$$Lambda$2(ChatActivity chatActivity, MessagesStorage messagesStorage, int i, CountDownLatch countDownLatch) {
        this.arg$1 = chatActivity;
        this.arg$2 = messagesStorage;
        this.arg$3 = i;
        this.arg$4 = countDownLatch;
    }

    public void run() {
        this.arg$1.lambda$onFragmentCreate$2$ChatActivity(this.arg$2, this.arg$3, this.arg$4);
    }
}
