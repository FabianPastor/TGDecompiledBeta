package org.telegram.ui;

import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.MessagesStorage;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda115 implements Runnable {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ MessagesStorage f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ CountDownLatch f$3;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda115(ChatActivity chatActivity, MessagesStorage messagesStorage, int i, CountDownLatch countDownLatch) {
        this.f$0 = chatActivity;
        this.f$1 = messagesStorage;
        this.f$2 = i;
        this.f$3 = countDownLatch;
    }

    public final void run() {
        this.f$0.lambda$onFragmentCreate$2(this.f$1, this.f$2, this.f$3);
    }
}
