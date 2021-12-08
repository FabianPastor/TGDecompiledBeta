package org.telegram.messenger;

import java.util.concurrent.CountDownLatch;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda87 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ Integer[] f$3;
    public final /* synthetic */ CountDownLatch f$4;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda87(MessagesStorage messagesStorage, boolean z, long j, Integer[] numArr, CountDownLatch countDownLatch) {
        this.f$0 = messagesStorage;
        this.f$1 = z;
        this.f$2 = j;
        this.f$3 = numArr;
        this.f$4 = countDownLatch;
    }

    public final void run() {
        this.f$0.m953x9011f9be(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
