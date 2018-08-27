package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class ChannelUsersActivity$$Lambda$5 implements RequestDelegate {
    private final ChannelUsersActivity arg$1;
    private final boolean arg$2;

    ChannelUsersActivity$$Lambda$5(ChannelUsersActivity channelUsersActivity, boolean z) {
        this.arg$1 = channelUsersActivity;
        this.arg$2 = z;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$getChannelParticipants$14$ChannelUsersActivity(this.arg$2, tLObject, tL_error);
    }
}
