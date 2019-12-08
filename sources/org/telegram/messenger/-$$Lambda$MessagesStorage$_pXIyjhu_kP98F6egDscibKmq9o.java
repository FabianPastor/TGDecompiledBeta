package org.telegram.messenger;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$_pXIyjhu_kP98F6egDscibKmq9o implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ ArrayList f$2;
    private final /* synthetic */ CountDownLatch f$3;

    public /* synthetic */ -$$Lambda$MessagesStorage$_pXIyjhu_kP98F6egDscibKmq9o(MessagesStorage messagesStorage, int i, ArrayList arrayList, CountDownLatch countDownLatch) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
        this.f$2 = arrayList;
        this.f$3 = countDownLatch;
    }

    public final void run() {
        this.f$0.lambda$getEncryptedChat$109$MessagesStorage(this.f$1, this.f$2, this.f$3);
    }
}
