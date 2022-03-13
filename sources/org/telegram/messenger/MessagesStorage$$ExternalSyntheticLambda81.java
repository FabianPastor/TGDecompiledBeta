package org.telegram.messenger;

import java.util.concurrent.CountDownLatch;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda81 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ boolean[] f$3;
    public final /* synthetic */ CountDownLatch f$4;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda81(MessagesStorage messagesStorage, long j, int i, boolean[] zArr, CountDownLatch countDownLatch) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
        this.f$2 = i;
        this.f$3 = zArr;
        this.f$4 = countDownLatch;
    }

    public final void run() {
        this.f$0.lambda$checkMessageId$123(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
