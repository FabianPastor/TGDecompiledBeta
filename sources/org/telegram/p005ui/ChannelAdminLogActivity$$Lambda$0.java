package org.telegram.p005ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.ChannelAdminLogActivity$$Lambda$0 */
final /* synthetic */ class ChannelAdminLogActivity$$Lambda$0 implements RequestDelegate {
    private final ChannelAdminLogActivity arg$1;

    ChannelAdminLogActivity$$Lambda$0(ChannelAdminLogActivity channelAdminLogActivity) {
        this.arg$1 = channelAdminLogActivity;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$loadMessages$1$ChannelAdminLogActivity(tLObject, tL_error);
    }
}
