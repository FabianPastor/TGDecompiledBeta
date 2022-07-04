package org.telegram.ui;

import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.MessagesStorage;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda91 implements Runnable {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ MessagesStorage f$1;
    public final /* synthetic */ CountDownLatch f$2;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda91(ChatActivity chatActivity, MessagesStorage messagesStorage, CountDownLatch countDownLatch) {
        this.f$0 = chatActivity;
        this.f$1 = messagesStorage;
        this.f$2 = countDownLatch;
    }

    public final void run() {
        this.f$0.m3030lambda$onFragmentCreate$9$orgtelegramuiChatActivity(this.f$1, this.f$2);
    }
}
