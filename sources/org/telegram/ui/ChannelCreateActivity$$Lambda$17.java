package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class ChannelCreateActivity$$Lambda$17 implements RequestDelegate {
    private final ChannelCreateActivity arg$1;

    ChannelCreateActivity$$Lambda$17(ChannelCreateActivity channelCreateActivity) {
        this.arg$1 = channelCreateActivity;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$13$ChannelCreateActivity(tLObject, tL_error);
    }
}
