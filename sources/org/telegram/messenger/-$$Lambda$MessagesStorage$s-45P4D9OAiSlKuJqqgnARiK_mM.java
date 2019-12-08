package org.telegram.messenger;

import java.util.concurrent.CountDownLatch;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$s-45P4D9OAiSlKuJqqgnARiK_mM implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ Object[] f$3;
    private final /* synthetic */ CountDownLatch f$4;

    public /* synthetic */ -$$Lambda$MessagesStorage$s-45P4D9OAiSlKuJqqgnARiK_mM(MessagesStorage messagesStorage, String str, int i, Object[] objArr, CountDownLatch countDownLatch) {
        this.f$0 = messagesStorage;
        this.f$1 = str;
        this.f$2 = i;
        this.f$3 = objArr;
        this.f$4 = countDownLatch;
    }

    public final void run() {
        this.f$0.lambda$getSentFile$97$MessagesStorage(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
