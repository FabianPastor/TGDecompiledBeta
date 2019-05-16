package org.telegram.messenger;

import java.util.concurrent.CountDownLatch;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$0uAjSDWjYqGf4fkJ0zE63NanRe0 implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ CountDownLatch f$2;
    private final /* synthetic */ boolean f$3;
    private final /* synthetic */ boolean f$4;

    public /* synthetic */ -$$Lambda$MessagesStorage$0uAjSDWjYqGf4fkJ0zE63NanRe0(MessagesStorage messagesStorage, int i, CountDownLatch countDownLatch, boolean z, boolean z2) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
        this.f$2 = countDownLatch;
        this.f$3 = z;
        this.f$4 = z2;
    }

    public final void run() {
        this.f$0.lambda$loadChatInfo$82$MessagesStorage(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
