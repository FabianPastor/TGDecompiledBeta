package org.telegram.messenger;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

final /* synthetic */ class MessagesStorage$$Lambda$68 implements Runnable {
    private final MessagesStorage arg$1;
    private final int arg$2;
    private final ArrayList arg$3;
    private final CountDownLatch arg$4;

    MessagesStorage$$Lambda$68(MessagesStorage messagesStorage, int i, ArrayList arrayList, CountDownLatch countDownLatch) {
        this.arg$1 = messagesStorage;
        this.arg$2 = i;
        this.arg$3 = arrayList;
        this.arg$4 = countDownLatch;
    }

    public void run() {
        this.arg$1.lambda$getEncryptedChat$92$MessagesStorage(this.arg$2, this.arg$3, this.arg$4);
    }
}
