package org.telegram.ui;

import java.util.concurrent.CountDownLatch;

final /* synthetic */ class ChannelEditInfoActivity$$Lambda$0 implements Runnable {
    private final ChannelEditInfoActivity arg$1;
    private final CountDownLatch arg$2;

    ChannelEditInfoActivity$$Lambda$0(ChannelEditInfoActivity channelEditInfoActivity, CountDownLatch countDownLatch) {
        this.arg$1 = channelEditInfoActivity;
        this.arg$2 = countDownLatch;
    }

    public void run() {
        this.arg$1.lambda$onFragmentCreate$0$ChannelEditInfoActivity(this.arg$2);
    }
}
