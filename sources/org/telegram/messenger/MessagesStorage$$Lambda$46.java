package org.telegram.messenger;

import java.util.concurrent.CountDownLatch;

final /* synthetic */ class MessagesStorage$$Lambda$46 implements Runnable {
    private final MessagesStorage arg$1;
    private final int arg$2;
    private final CountDownLatch arg$3;
    private final boolean arg$4;
    private final boolean arg$5;

    MessagesStorage$$Lambda$46(MessagesStorage messagesStorage, int i, CountDownLatch countDownLatch, boolean z, boolean z2) {
        this.arg$1 = messagesStorage;
        this.arg$2 = i;
        this.arg$3 = countDownLatch;
        this.arg$4 = z;
        this.arg$5 = z2;
    }

    public void run() {
        this.arg$1.lambda$loadChatInfo$68$MessagesStorage(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
