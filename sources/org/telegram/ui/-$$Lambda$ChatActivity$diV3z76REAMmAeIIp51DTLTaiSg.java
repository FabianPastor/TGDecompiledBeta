package org.telegram.ui;

import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.MessagesStorage;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$diV3z76REAMmAeIIp51DTLTaiSg implements Runnable {
    private final /* synthetic */ ChatActivity f$0;
    private final /* synthetic */ MessagesStorage f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ CountDownLatch f$3;

    public /* synthetic */ -$$Lambda$ChatActivity$diV3z76REAMmAeIIp51DTLTaiSg(ChatActivity chatActivity, MessagesStorage messagesStorage, int i, CountDownLatch countDownLatch) {
        this.f$0 = chatActivity;
        this.f$1 = messagesStorage;
        this.f$2 = i;
        this.f$3 = countDownLatch;
    }

    public final void run() {
        this.f$0.lambda$onFragmentCreate$0$ChatActivity(this.f$1, this.f$2, this.f$3);
    }
}
