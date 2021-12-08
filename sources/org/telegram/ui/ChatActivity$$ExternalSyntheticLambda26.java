package org.telegram.ui;

import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.MessagesStorage;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda26 implements Runnable {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ MessagesStorage f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ CountDownLatch f$3;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda26(ChatActivity chatActivity, MessagesStorage messagesStorage, long j, CountDownLatch countDownLatch) {
        this.f$0 = chatActivity;
        this.f$1 = messagesStorage;
        this.f$2 = j;
        this.f$3 = countDownLatch;
    }

    public final void run() {
        this.f$0.m1746lambda$onFragmentCreate$6$orgtelegramuiChatActivity(this.f$1, this.f$2, this.f$3);
    }
}
