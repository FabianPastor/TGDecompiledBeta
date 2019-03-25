package org.telegram.messenger;

import java.util.concurrent.CountDownLatch;

final /* synthetic */ class MessagesStorage$$Lambda$67 implements Runnable {
    private final MessagesStorage arg$1;
    private final String arg$2;
    private final int arg$3;
    private final Object[] arg$4;
    private final CountDownLatch arg$5;

    MessagesStorage$$Lambda$67(MessagesStorage messagesStorage, String str, int i, Object[] objArr, CountDownLatch countDownLatch) {
        this.arg$1 = messagesStorage;
        this.arg$2 = str;
        this.arg$3 = i;
        this.arg$4 = objArr;
        this.arg$5 = countDownLatch;
    }

    public void run() {
        this.arg$1.lambda$getSentFile$95$MessagesStorage(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
