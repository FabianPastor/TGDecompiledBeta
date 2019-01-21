package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class ChannelCreateActivity$$Lambda$7 implements RequestDelegate {
    private final ChannelCreateActivity arg$1;

    ChannelCreateActivity$$Lambda$7(ChannelCreateActivity channelCreateActivity) {
        this.arg$1 = channelCreateActivity;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$generateLink$10$ChannelCreateActivity(tLObject, tL_error);
    }
}
