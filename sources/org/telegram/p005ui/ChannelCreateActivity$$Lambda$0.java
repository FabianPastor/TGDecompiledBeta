package org.telegram.p005ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.ChannelCreateActivity$$Lambda$0 */
final /* synthetic */ class ChannelCreateActivity$$Lambda$0 implements RequestDelegate {
    private final ChannelCreateActivity arg$1;

    ChannelCreateActivity$$Lambda$0(ChannelCreateActivity channelCreateActivity) {
        this.arg$1 = channelCreateActivity;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$new$1$ChannelCreateActivity(tLObject, tL_error);
    }
}
