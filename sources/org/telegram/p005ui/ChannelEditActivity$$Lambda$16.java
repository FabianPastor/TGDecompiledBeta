package org.telegram.p005ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.ChannelEditActivity$$Lambda$16 */
final /* synthetic */ class ChannelEditActivity$$Lambda$16 implements RequestDelegate {
    private final ChannelEditActivity arg$1;

    ChannelEditActivity$$Lambda$16(ChannelEditActivity channelEditActivity) {
        this.arg$1 = channelEditActivity;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$generateLink$21$ChannelEditActivity(tLObject, tL_error);
    }
}
