package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class ChannelEditInfoActivity$$Lambda$18 implements RequestDelegate {
    private final ChannelEditInfoActivity arg$1;
    private final String arg$2;

    ChannelEditInfoActivity$$Lambda$18(ChannelEditInfoActivity channelEditInfoActivity, String str) {
        this.arg$1 = channelEditInfoActivity;
        this.arg$2 = str;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$23$ChannelEditInfoActivity(this.arg$2, tLObject, tL_error);
    }
}
