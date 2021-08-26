package org.telegram.messenger;

import java.util.concurrent.CountDownLatch;
import org.telegram.tgnet.TLRPC$User;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda180 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ TLRPC$User[] f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ CountDownLatch f$3;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda180(MessagesStorage messagesStorage, TLRPC$User[] tLRPC$UserArr, int i, CountDownLatch countDownLatch) {
        this.f$0 = messagesStorage;
        this.f$1 = tLRPC$UserArr;
        this.f$2 = i;
        this.f$3 = countDownLatch;
    }

    public final void run() {
        this.f$0.lambda$getUserSync$183(this.f$1, this.f$2, this.f$3);
    }
}
