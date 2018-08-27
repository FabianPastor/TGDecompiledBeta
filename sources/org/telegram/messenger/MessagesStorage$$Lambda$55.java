package org.telegram.messenger;

import java.util.concurrent.CountDownLatch;

final /* synthetic */ class MessagesStorage$$Lambda$55 implements Runnable {
    private final MessagesStorage arg$1;
    private final long arg$2;
    private final boolean[] arg$3;
    private final CountDownLatch arg$4;

    MessagesStorage$$Lambda$55(MessagesStorage messagesStorage, long j, boolean[] zArr, CountDownLatch countDownLatch) {
        this.arg$1 = messagesStorage;
        this.arg$2 = j;
        this.arg$3 = zArr;
        this.arg$4 = countDownLatch;
    }

    public void run() {
        this.arg$1.lambda$checkMessageByRandomId$77$MessagesStorage(this.arg$2, this.arg$3, this.arg$4);
    }
}
