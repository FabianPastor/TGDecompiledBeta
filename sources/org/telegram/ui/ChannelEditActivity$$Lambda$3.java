package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class ChannelEditActivity$$Lambda$3 implements RequestDelegate {
    private final ChannelEditActivity arg$1;
    private final TL_channels_getParticipants arg$2;
    private final int arg$3;

    ChannelEditActivity$$Lambda$3(ChannelEditActivity channelEditActivity, TL_channels_getParticipants tL_channels_getParticipants, int i) {
        this.arg$1 = channelEditActivity;
        this.arg$2 = tL_channels_getParticipants;
        this.arg$3 = i;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$getChannelParticipants$4$ChannelEditActivity(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
