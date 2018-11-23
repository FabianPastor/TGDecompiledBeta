package org.telegram.p005ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.ChannelEditInfoActivity$$Lambda$15 */
final /* synthetic */ class ChannelEditInfoActivity$$Lambda$15 implements RequestDelegate {
    private final ChannelEditInfoActivity arg$1;

    ChannelEditInfoActivity$$Lambda$15(ChannelEditInfoActivity channelEditInfoActivity) {
        this.arg$1 = channelEditInfoActivity;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$7$ChannelEditInfoActivity(tLObject, tL_error);
    }
}
