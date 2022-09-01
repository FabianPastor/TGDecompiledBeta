package org.telegram.messenger;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda89 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ AtomicReference f$3;
    public final /* synthetic */ CountDownLatch f$4;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda89(MessagesStorage messagesStorage, long j, long j2, AtomicReference atomicReference, CountDownLatch countDownLatch) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
        this.f$2 = j2;
        this.f$3 = atomicReference;
        this.f$4 = countDownLatch;
    }

    public final void run() {
        this.f$0.lambda$getMessage$109(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
