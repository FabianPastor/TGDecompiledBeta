package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class ChannelEditInfoActivity$$Lambda$17 implements Runnable {
    private final ChannelEditInfoActivity arg$1;
    private final TL_error arg$2;
    private final TLObject arg$3;

    ChannelEditInfoActivity$$Lambda$17(ChannelEditInfoActivity channelEditInfoActivity, TL_error tL_error, TLObject tLObject) {
        this.arg$1 = channelEditInfoActivity;
        this.arg$2 = tL_error;
        this.arg$3 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$25$ChannelEditInfoActivity(this.arg$2, this.arg$3);
    }
}
