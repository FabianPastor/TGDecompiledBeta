package org.telegram.ui;

import java.util.concurrent.CountDownLatch;

final /* synthetic */ class ChatEditTypeActivity$$Lambda$0 implements Runnable {
    private final ChatEditTypeActivity arg$1;
    private final CountDownLatch arg$2;

    ChatEditTypeActivity$$Lambda$0(ChatEditTypeActivity chatEditTypeActivity, CountDownLatch countDownLatch) {
        this.arg$1 = chatEditTypeActivity;
        this.arg$2 = countDownLatch;
    }

    public void run() {
        this.arg$1.lambda$onFragmentCreate$0$ChatEditTypeActivity(this.arg$2);
    }
}
