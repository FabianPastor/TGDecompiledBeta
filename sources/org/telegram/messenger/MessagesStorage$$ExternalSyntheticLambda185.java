package org.telegram.messenger;

import java.util.concurrent.CountDownLatch;
import org.telegram.tgnet.TLRPC$User;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda185 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ TLRPC$User[] f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ CountDownLatch f$3;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda185(MessagesStorage messagesStorage, TLRPC$User[] tLRPC$UserArr, long j, CountDownLatch countDownLatch) {
        this.f$0 = messagesStorage;
        this.f$1 = tLRPC$UserArr;
        this.f$2 = j;
        this.f$3 = countDownLatch;
    }

    public final void run() {
        this.f$0.lambda$getUserSync$188(this.f$1, this.f$2, this.f$3);
    }
}
