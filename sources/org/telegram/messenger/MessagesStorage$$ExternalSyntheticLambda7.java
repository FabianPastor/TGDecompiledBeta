package org.telegram.messenger;

import java.util.concurrent.CountDownLatch;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda7 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ Integer[] f$2;
    public final /* synthetic */ CountDownLatch f$3;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda7(MessagesStorage messagesStorage, long j, Integer[] numArr, CountDownLatch countDownLatch) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
        this.f$2 = numArr;
        this.f$3 = countDownLatch;
    }

    public final void run() {
        this.f$0.m946x6e5ff8ca(this.f$1, this.f$2, this.f$3);
    }
}
