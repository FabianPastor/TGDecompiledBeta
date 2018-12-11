package org.telegram.p005ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.ChannelEditActivity$$Lambda$18 */
final /* synthetic */ class ChannelEditActivity$$Lambda$18 implements Runnable {
    private final ChannelEditActivity arg$1;
    private final TL_error arg$2;
    private final TLObject arg$3;

    ChannelEditActivity$$Lambda$18(ChannelEditActivity channelEditActivity, TL_error tL_error, TLObject tLObject) {
        this.arg$1 = channelEditActivity;
        this.arg$2 = tL_error;
        this.arg$3 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$20$ChannelEditActivity(this.arg$2, this.arg$3);
    }
}
