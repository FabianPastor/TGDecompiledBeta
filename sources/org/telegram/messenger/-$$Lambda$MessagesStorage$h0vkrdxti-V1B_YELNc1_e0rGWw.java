package org.telegram.messenger;

import java.util.concurrent.CountDownLatch;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$h0vkrdxti-V1B_YELNc1_e0rGWw implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ boolean f$2;
    private final /* synthetic */ long f$3;
    private final /* synthetic */ boolean[] f$4;
    private final /* synthetic */ CountDownLatch f$5;

    public /* synthetic */ -$$Lambda$MessagesStorage$h0vkrdxti-V1B_YELNc1_e0rGWw(MessagesStorage messagesStorage, int i, boolean z, long j, boolean[] zArr, CountDownLatch countDownLatch) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
        this.f$2 = z;
        this.f$3 = j;
        this.f$4 = zArr;
        this.f$5 = countDownLatch;
    }

    public final void run() {
        this.f$0.lambda$checkMessageId$89$MessagesStorage(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
