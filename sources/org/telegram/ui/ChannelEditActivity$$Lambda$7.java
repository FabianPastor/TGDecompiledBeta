package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class ChannelEditActivity$$Lambda$7 implements Runnable {
    private final ChannelEditActivity arg$1;
    private final TL_error arg$2;
    private final TLObject arg$3;
    private final TL_channels_getParticipants arg$4;

    ChannelEditActivity$$Lambda$7(ChannelEditActivity channelEditActivity, TL_error tL_error, TLObject tLObject, TL_channels_getParticipants tL_channels_getParticipants) {
        this.arg$1 = channelEditActivity;
        this.arg$2 = tL_error;
        this.arg$3 = tLObject;
        this.arg$4 = tL_channels_getParticipants;
    }

    public void run() {
        this.arg$1.lambda$null$3$ChannelEditActivity(this.arg$2, this.arg$3, this.arg$4);
    }
}
