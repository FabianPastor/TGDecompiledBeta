package org.telegram.messenger;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda99 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ CountDownLatch f$3;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda99(MessagesStorage messagesStorage, long j, ArrayList arrayList, CountDownLatch countDownLatch) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
        this.f$2 = arrayList;
        this.f$3 = countDownLatch;
    }

    public final void run() {
        this.f$0.lambda$getEncryptedChat$140(this.f$1, this.f$2, this.f$3);
    }
}
