package org.telegram.messenger;

import java.util.concurrent.CountDownLatch;
import org.telegram.tgnet.TLRPC.User;

final /* synthetic */ class MessagesStorage$$Lambda$97 implements Runnable {
    private final MessagesStorage arg$1;
    private final User[] arg$2;
    private final int arg$3;
    private final CountDownLatch arg$4;

    MessagesStorage$$Lambda$97(MessagesStorage messagesStorage, User[] userArr, int i, CountDownLatch countDownLatch) {
        this.arg$1 = messagesStorage;
        this.arg$2 = userArr;
        this.arg$3 = i;
        this.arg$4 = countDownLatch;
    }

    public void run() {
        this.arg$1.lambda$getUserSync$125$MessagesStorage(this.arg$2, this.arg$3, this.arg$4);
    }
}
