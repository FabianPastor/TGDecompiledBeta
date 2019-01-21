package org.telegram.ui;

import java.util.concurrent.CountDownLatch;

final /* synthetic */ class ChatEditActivity$$Lambda$0 implements Runnable {
    private final ChatEditActivity arg$1;
    private final CountDownLatch arg$2;

    ChatEditActivity$$Lambda$0(ChatEditActivity chatEditActivity, CountDownLatch countDownLatch) {
        this.arg$1 = chatEditActivity;
        this.arg$2 = countDownLatch;
    }

    public void run() {
        this.arg$1.lambda$onFragmentCreate$0$ChatEditActivity(this.arg$2);
    }
}
