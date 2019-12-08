package org.telegram.messenger;

import java.util.concurrent.CountDownLatch;
import org.telegram.tgnet.TLRPC.ChatFull;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$bFUukskWZw3OKUlOSgkXj-ylMEo implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ ChatFull[] f$2;
    private final /* synthetic */ boolean f$3;
    private final /* synthetic */ boolean f$4;
    private final /* synthetic */ CountDownLatch f$5;

    public /* synthetic */ -$$Lambda$MessagesStorage$bFUukskWZw3OKUlOSgkXj-ylMEo(MessagesStorage messagesStorage, int i, ChatFull[] chatFullArr, boolean z, boolean z2, CountDownLatch countDownLatch) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
        this.f$2 = chatFullArr;
        this.f$3 = z;
        this.f$4 = z2;
        this.f$5 = countDownLatch;
    }

    public final void run() {
        this.f$0.lambda$loadChatInfo$79$MessagesStorage(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
