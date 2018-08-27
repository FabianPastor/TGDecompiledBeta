package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class ChannelUsersActivity$$Lambda$10 implements RequestDelegate {
    private final ChannelUsersActivity arg$1;

    ChannelUsersActivity$$Lambda$10(ChannelUsersActivity channelUsersActivity) {
        this.arg$1 = channelUsersActivity;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$8$ChannelUsersActivity(tLObject, tL_error);
    }
}
