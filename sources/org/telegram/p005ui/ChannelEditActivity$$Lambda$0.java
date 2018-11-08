package org.telegram.p005ui;

import java.util.concurrent.CountDownLatch;

/* renamed from: org.telegram.ui.ChannelEditActivity$$Lambda$0 */
final /* synthetic */ class ChannelEditActivity$$Lambda$0 implements Runnable {
    private final ChannelEditActivity arg$1;
    private final CountDownLatch arg$2;

    ChannelEditActivity$$Lambda$0(ChannelEditActivity channelEditActivity, CountDownLatch countDownLatch) {
        this.arg$1 = channelEditActivity;
        this.arg$2 = countDownLatch;
    }

    public void run() {
        this.arg$1.lambda$onFragmentCreate$0$ChannelEditActivity(this.arg$2);
    }
}
