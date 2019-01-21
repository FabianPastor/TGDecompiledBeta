package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class ChannelCreateActivity$$Lambda$21 implements Runnable {
    private final ChannelCreateActivity arg$1;
    private final TL_error arg$2;

    ChannelCreateActivity$$Lambda$21(ChannelCreateActivity channelCreateActivity, TL_error tL_error) {
        this.arg$1 = channelCreateActivity;
        this.arg$2 = tL_error;
    }

    public void run() {
        this.arg$1.lambda$null$0$ChannelCreateActivity(this.arg$2);
    }
}
