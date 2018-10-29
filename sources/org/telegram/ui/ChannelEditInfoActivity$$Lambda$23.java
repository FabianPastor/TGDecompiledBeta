package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class ChannelEditInfoActivity$$Lambda$23 implements RequestDelegate {
    private final ChannelEditInfoActivity arg$1;

    ChannelEditInfoActivity$$Lambda$23(ChannelEditInfoActivity channelEditInfoActivity) {
        this.arg$1 = channelEditInfoActivity;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$17$ChannelEditInfoActivity(tLObject, tL_error);
    }
}
