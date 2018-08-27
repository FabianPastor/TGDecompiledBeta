package org.telegram.messenger;

import java.util.concurrent.CountDownLatch;
import org.telegram.tgnet.TLRPC.Chat;

final /* synthetic */ class MessagesStorage$$Lambda$98 implements Runnable {
    private final MessagesStorage arg$1;
    private final Chat[] arg$2;
    private final int arg$3;
    private final CountDownLatch arg$4;

    MessagesStorage$$Lambda$98(MessagesStorage messagesStorage, Chat[] chatArr, int i, CountDownLatch countDownLatch) {
        this.arg$1 = messagesStorage;
        this.arg$2 = chatArr;
        this.arg$3 = i;
        this.arg$4 = countDownLatch;
    }

    public void run() {
        this.arg$1.lambda$getChatSync$126$MessagesStorage(this.arg$2, this.arg$3, this.arg$4);
    }
}
