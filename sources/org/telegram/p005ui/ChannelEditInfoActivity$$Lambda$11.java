package org.telegram.p005ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.ChannelEditInfoActivity$$Lambda$11 */
final /* synthetic */ class ChannelEditInfoActivity$$Lambda$11 implements Runnable {
    private final ChannelEditInfoActivity arg$1;
    private final String arg$2;
    private final TL_error arg$3;
    private final TLObject arg$4;

    ChannelEditInfoActivity$$Lambda$11(ChannelEditInfoActivity channelEditInfoActivity, String str, TL_error tL_error, TLObject tLObject) {
        this.arg$1 = channelEditInfoActivity;
        this.arg$2 = str;
        this.arg$3 = tL_error;
        this.arg$4 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$12$ChannelEditInfoActivity(this.arg$2, this.arg$3, this.arg$4);
    }
}
