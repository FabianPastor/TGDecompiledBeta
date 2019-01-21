package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_channels_adminLogResults;

final /* synthetic */ class ChannelAdminLogActivity$$Lambda$14 implements Runnable {
    private final ChannelAdminLogActivity arg$1;
    private final TL_channels_adminLogResults arg$2;

    ChannelAdminLogActivity$$Lambda$14(ChannelAdminLogActivity channelAdminLogActivity, TL_channels_adminLogResults tL_channels_adminLogResults) {
        this.arg$1 = channelAdminLogActivity;
        this.arg$2 = tL_channels_adminLogResults;
    }

    public void run() {
        this.arg$1.lambda$null$0$ChannelAdminLogActivity(this.arg$2);
    }
}
