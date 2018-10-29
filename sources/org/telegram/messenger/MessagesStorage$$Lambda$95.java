package org.telegram.messenger;

import java.util.concurrent.CountDownLatch;

final /* synthetic */ class MessagesStorage$$Lambda$95 implements Runnable {
    private final MessagesStorage arg$1;
    private final boolean arg$2;
    private final long arg$3;
    private final Integer[] arg$4;
    private final CountDownLatch arg$5;

    MessagesStorage$$Lambda$95(MessagesStorage messagesStorage, boolean z, long j, Integer[] numArr, CountDownLatch countDownLatch) {
        this.arg$1 = messagesStorage;
        this.arg$2 = z;
        this.arg$3 = j;
        this.arg$4 = numArr;
        this.arg$5 = countDownLatch;
    }

    public void run() {
        this.arg$1.lambda$getDialogReadMax$123$MessagesStorage(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
