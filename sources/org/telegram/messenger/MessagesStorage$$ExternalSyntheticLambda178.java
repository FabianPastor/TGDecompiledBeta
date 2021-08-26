package org.telegram.messenger;

import java.util.concurrent.CountDownLatch;
import org.telegram.tgnet.TLRPC$Chat;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda178 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ TLRPC$Chat[] f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ CountDownLatch f$3;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda178(MessagesStorage messagesStorage, TLRPC$Chat[] tLRPC$ChatArr, int i, CountDownLatch countDownLatch) {
        this.f$0 = messagesStorage;
        this.f$1 = tLRPC$ChatArr;
        this.f$2 = i;
        this.f$3 = countDownLatch;
    }

    public final void run() {
        this.f$0.lambda$getChatSync$184(this.f$1, this.f$2, this.f$3);
    }
}
