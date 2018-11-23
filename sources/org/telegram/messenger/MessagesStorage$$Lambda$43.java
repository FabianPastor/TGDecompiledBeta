package org.telegram.messenger;

import java.util.concurrent.CountDownLatch;
import org.telegram.tgnet.TLRPC.User;

final /* synthetic */ class MessagesStorage$$Lambda$43 implements Runnable {
    private final MessagesStorage arg$1;
    private final User arg$2;
    private final CountDownLatch arg$3;
    private final boolean arg$4;

    MessagesStorage$$Lambda$43(MessagesStorage messagesStorage, User user, CountDownLatch countDownLatch, boolean z) {
        this.arg$1 = messagesStorage;
        this.arg$2 = user;
        this.arg$3 = countDownLatch;
        this.arg$4 = z;
    }

    public void run() {
        this.arg$1.lambda$loadUserInfo$63$MessagesStorage(this.arg$2, this.arg$3, this.arg$4);
    }
}
