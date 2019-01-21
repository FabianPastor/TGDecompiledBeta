package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class ChannelCreateActivity$$Lambda$12 implements RequestDelegate {
    private final ChannelCreateActivity arg$1;
    private final String arg$2;

    ChannelCreateActivity$$Lambda$12(ChannelCreateActivity channelCreateActivity, String str) {
        this.arg$1 = channelCreateActivity;
        this.arg$2 = str;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$19$ChannelCreateActivity(this.arg$2, tLObject, tL_error);
    }
}
