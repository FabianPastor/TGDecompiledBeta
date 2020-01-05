package org.telegram.messenger;

import java.util.concurrent.CountDownLatch;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$TKjSbp1HaK8KS8Uw3W_qpmrASbQ implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ Integer[] f$2;
    private final /* synthetic */ CountDownLatch f$3;

    public /* synthetic */ -$$Lambda$MessagesStorage$TKjSbp1HaK8KS8Uw3W_qpmrASbQ(MessagesStorage messagesStorage, int i, Integer[] numArr, CountDownLatch countDownLatch) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
        this.f$2 = numArr;
        this.f$3 = countDownLatch;
    }

    public final void run() {
        this.f$0.lambda$getChannelPtsSync$146$MessagesStorage(this.f$1, this.f$2, this.f$3);
    }
}
