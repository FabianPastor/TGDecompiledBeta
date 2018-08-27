package org.telegram.messenger;

import java.util.concurrent.CountDownLatch;

final /* synthetic */ class MessagesStorage$$Lambda$56 implements Runnable {
    private final MessagesStorage arg$1;
    private final long arg$2;
    private final int arg$3;
    private final boolean[] arg$4;
    private final CountDownLatch arg$5;

    MessagesStorage$$Lambda$56(MessagesStorage messagesStorage, long j, int i, boolean[] zArr, CountDownLatch countDownLatch) {
        this.arg$1 = messagesStorage;
        this.arg$2 = j;
        this.arg$3 = i;
        this.arg$4 = zArr;
        this.arg$5 = countDownLatch;
    }

    public void run() {
        this.arg$1.lambda$checkMessageId$78$MessagesStorage(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
