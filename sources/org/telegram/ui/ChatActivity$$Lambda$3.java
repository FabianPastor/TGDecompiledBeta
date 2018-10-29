package org.telegram.ui;

import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.MessagesStorage;

final /* synthetic */ class ChatActivity$$Lambda$3 implements Runnable {
    private final ChatActivity arg$1;
    private final MessagesStorage arg$2;
    private final CountDownLatch arg$3;

    ChatActivity$$Lambda$3(ChatActivity chatActivity, MessagesStorage messagesStorage, CountDownLatch countDownLatch) {
        this.arg$1 = chatActivity;
        this.arg$2 = messagesStorage;
        this.arg$3 = countDownLatch;
    }

    public void run() {
        this.arg$1.lambda$onFragmentCreate$3$ChatActivity(this.arg$2, this.arg$3);
    }
}
