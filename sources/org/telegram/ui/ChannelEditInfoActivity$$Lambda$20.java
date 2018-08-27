package org.telegram.ui;

import org.telegram.tgnet.TLObject;

final /* synthetic */ class ChannelEditInfoActivity$$Lambda$20 implements Runnable {
    private final ChannelEditInfoActivity arg$1;
    private final TLObject arg$2;

    ChannelEditInfoActivity$$Lambda$20(ChannelEditInfoActivity channelEditInfoActivity, TLObject tLObject) {
        this.arg$1 = channelEditInfoActivity;
        this.arg$2 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$20$ChannelEditInfoActivity(this.arg$2);
    }
}
