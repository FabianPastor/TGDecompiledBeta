package org.telegram.messenger;

import java.util.concurrent.CountDownLatch;
import org.telegram.tgnet.TLRPC.Chat;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$FPm2wKLMGEaTwBiPw8dp3fwp1t0 implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ Chat[] f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ CountDownLatch f$3;

    public /* synthetic */ -$$Lambda$MessagesStorage$FPm2wKLMGEaTwBiPw8dp3fwp1t0(MessagesStorage messagesStorage, Chat[] chatArr, int i, CountDownLatch countDownLatch) {
        this.f$0 = messagesStorage;
        this.f$1 = chatArr;
        this.f$2 = i;
        this.f$3 = countDownLatch;
    }

    public final void run() {
        this.f$0.lambda$getChatSync$150$MessagesStorage(this.f$1, this.f$2, this.f$3);
    }
}