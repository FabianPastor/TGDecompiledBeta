package org.telegram.messenger;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

final /* synthetic */ class MessagesStorage$$Lambda$60 implements Runnable {
    private final MessagesStorage arg$1;
    private final String arg$2;
    private final int arg$3;
    private final ArrayList arg$4;
    private final CountDownLatch arg$5;

    MessagesStorage$$Lambda$60(MessagesStorage messagesStorage, String str, int i, ArrayList arrayList, CountDownLatch countDownLatch) {
        this.arg$1 = messagesStorage;
        this.arg$2 = str;
        this.arg$3 = i;
        this.arg$4 = arrayList;
        this.arg$5 = countDownLatch;
    }

    public void run() {
        this.arg$1.lambda$getSentFile$84$MessagesStorage(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
