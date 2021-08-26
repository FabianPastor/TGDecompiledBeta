package org.telegram.messenger;

import java.util.concurrent.CountDownLatch;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda68 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ long f$3;
    public final /* synthetic */ boolean[] f$4;
    public final /* synthetic */ CountDownLatch f$5;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda68(MessagesStorage messagesStorage, int i, boolean z, long j, boolean[] zArr, CountDownLatch countDownLatch) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
        this.f$2 = z;
        this.f$3 = j;
        this.f$4 = zArr;
        this.f$5 = countDownLatch;
    }

    public final void run() {
        this.f$0.lambda$checkMessageId$107(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
