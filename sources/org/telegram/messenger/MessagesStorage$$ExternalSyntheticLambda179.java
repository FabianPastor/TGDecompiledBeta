package org.telegram.messenger;

import java.util.concurrent.CountDownLatch;
import org.telegram.tgnet.TLRPC$ChatFull;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda179 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ TLRPC$ChatFull[] f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ boolean f$4;
    public final /* synthetic */ boolean f$5;
    public final /* synthetic */ int f$6;
    public final /* synthetic */ CountDownLatch f$7;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda179(MessagesStorage messagesStorage, TLRPC$ChatFull[] tLRPC$ChatFullArr, int i, boolean z, boolean z2, boolean z3, int i2, CountDownLatch countDownLatch) {
        this.f$0 = messagesStorage;
        this.f$1 = tLRPC$ChatFullArr;
        this.f$2 = i;
        this.f$3 = z;
        this.f$4 = z2;
        this.f$5 = z3;
        this.f$6 = i2;
        this.f$7 = countDownLatch;
    }

    public final void run() {
        this.f$0.lambda$loadChatInfo$97(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}
