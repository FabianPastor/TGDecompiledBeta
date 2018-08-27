package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class ChannelUsersActivity$$Lambda$7 implements Runnable {
    private final ChannelUsersActivity arg$1;
    private final TL_error arg$2;
    private final TLObject arg$3;
    private final boolean arg$4;

    ChannelUsersActivity$$Lambda$7(ChannelUsersActivity channelUsersActivity, TL_error tL_error, TLObject tLObject, boolean z) {
        this.arg$1 = channelUsersActivity;
        this.arg$2 = tL_error;
        this.arg$3 = tLObject;
        this.arg$4 = z;
    }

    public void run() {
        this.arg$1.lambda$null$13$ChannelUsersActivity(this.arg$2, this.arg$3, this.arg$4);
    }
}
