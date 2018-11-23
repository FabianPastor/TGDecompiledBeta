package org.telegram.p005ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.ChannelAdminLogActivity$$Lambda$9 */
final /* synthetic */ class ChannelAdminLogActivity$$Lambda$9 implements Runnable {
    private final ChannelAdminLogActivity arg$1;
    private final TL_error arg$2;
    private final TLObject arg$3;

    ChannelAdminLogActivity$$Lambda$9(ChannelAdminLogActivity channelAdminLogActivity, TL_error tL_error, TLObject tLObject) {
        this.arg$1 = channelAdminLogActivity;
        this.arg$2 = tL_error;
        this.arg$3 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$12$ChannelAdminLogActivity(this.arg$2, this.arg$3);
    }
}
